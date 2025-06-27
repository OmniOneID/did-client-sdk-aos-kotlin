/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.sdk.wallet

import android.content.Context
import android.util.Log
import org.omnione.did.sdk.datamodel.protocol.P210ResponseVo
import org.omnione.did.sdk.datamodel.protocol.P220ResponseVo
import org.omnione.did.sdk.datamodel.did.DidDocVo
import org.omnione.did.sdk.datamodel.security.AccE2e
import org.omnione.did.sdk.datamodel.security.DIDAuth
import org.omnione.did.sdk.datamodel.vc.issue.ReqRevokeVC
import org.omnione.did.sdk.datamodel.vc.issue.ReqVC
import org.omnione.did.sdk.datamodel.token.SignedWalletInfo
import org.omnione.did.sdk.datamodel.vc.issue.ReturnEncVP
import org.omnione.did.sdk.datamodel.common.enums.RoleType
import org.omnione.did.sdk.communication.exception.CommunicationException
import org.omnione.did.sdk.datamodel.common.enums.SymmetricCipherType
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType
import org.omnione.did.sdk.datamodel.profile.IssueProfile
import org.omnione.did.sdk.datamodel.profile.ReqE2e
import org.omnione.did.sdk.datamodel.util.MessageUtil
import org.omnione.did.sdk.datamodel.protocol.P131RequestVo
import org.omnione.did.sdk.datamodel.did.AttestedDidDoc
import org.omnione.did.sdk.datamodel.did.SignedDidDoc
import org.omnione.did.sdk.datamodel.token.Wallet
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.common.enums.ProofPurpose
import org.omnione.did.sdk.datamodel.common.enums.ProofType
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import org.omnione.did.sdk.datamodel.vcschema.VCSchema
import org.omnione.did.sdk.datamodel.vp.VerifiablePresentation
import org.omnione.did.sdk.datamodel.wallet.AllowedCAList
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.DataModels.CipherInfo
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DataModels.EcKeyPair
import org.omnione.did.sdk.utility.DataModels.EcType
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.utility.MultibaseUtils
import org.omnione.did.sdk.wallet.walletservice.config.Constants
import org.omnione.did.sdk.wallet.walletservice.db.CaPkg
import org.omnione.did.sdk.wallet.walletservice.db.DBManager
import org.omnione.did.sdk.wallet.walletservice.db.Preference
import org.omnione.did.sdk.wallet.walletservice.db.User
import org.omnione.did.sdk.wallet.walletservice.exception.WalletErrorCode
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.wallet.walletservice.logger.WalletLogger
import org.omnione.did.sdk.wallet.walletservice.network.HttpUrlConnection
import org.omnione.did.sdk.wallet.walletservice.network.protocol.IssueVc
import org.omnione.did.sdk.wallet.walletservice.network.protocol.RegUser
import org.omnione.did.sdk.wallet.walletservice.network.protocol.RestoreUser
import org.omnione.did.sdk.wallet.walletservice.network.protocol.RevokeVc
import org.omnione.did.sdk.wallet.walletservice.network.protocol.UpdateUser
import org.omnione.did.sdk.core.bioprompthelper.BioPromptHelper
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.vcmanager.datamodel.ClaimInfo
import org.omnione.did.sdk.core.vcmanager.datamodel.PresentationInfo
import org.omnione.did.sdk.wallet.walletservice.util.WalletUtil
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException

class WalletService : WalletServiceInterface {
    private var context: Context? = null
    private lateinit var bioPromptHelper: BioPromptHelper
    private lateinit var walletCore: WalletCore
    private lateinit var walletLogger: WalletLogger
    private var bioPromptInterface: BioPromptHelper.BioPromptInterface? = null

    fun setBioPromptListener(bioPromptInterface: BioPromptHelper.BioPromptInterface) {
        this.bioPromptInterface = bioPromptInterface
    }

    interface BioPromptInterface {
        fun onSuccess(result: String)
        fun onFail(result: String)
    }

    constructor()

    constructor(context: Context, walletCore: WalletCore) {
        this.context = context
        this.walletCore = walletCore
        bioPromptHelper = BioPromptHelper(context)
        walletLogger = WalletLogger.getInstance()
    }

    @Throws(WalletException::class, ExecutionException::class, InterruptedException::class)
    override fun fetchCaInfo(tasUrl: String) {
        tasUrl.takeIf { it.isNotEmpty() } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "tasUrl"
        )

        val pkgNameList = WalletUtil.getAllowedCa(tasUrl).get()
        val allowedCAList = MessageUtil.deserialize(pkgNameList, AllowedCAList::class.java)

        if (allowedCAList.count == 0)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_CREATE_WALLET_FAIL)

        val allowedCAPkgList = Array(allowedCAList.count) { "" }.also { pkgList ->
            for (i in 0 until allowedCAList.count) {
                pkgList[i] = allowedCAList.items[i]
            }
        }

        WalletLogger.d("allowed CA pkgName : " + allowedCAPkgList[0])

        Thread {
            context?.let { ctx ->
                DBManager.getDatabases(ctx).let { walletDB ->
                    walletDB.caPkgDao()?.insertAll(CaPkg(allowedCAPkgList[0]))
                }
            }
        }.start()
    }

    @Throws(
        WalletException::class,
        WalletCoreException::class,
        UtilityException::class,
        ExecutionException::class,
        InterruptedException::class
    )
    override fun createDeviceDocument(walletUrl: String, tasUrl: String) {
        walletUrl.takeIf { it.isNotEmpty() } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "walletUrl"
        )

        tasUrl.takeIf { it.isNotEmpty() } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "tasUrl"
        )

        val deviceDIDDoc = walletCore.createDeviceDIDDoc()
        val did = deviceDIDDoc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "deviceDIDDoc.id"
        )
        val keyIds = listOf(Constants.KEY_ID_ASSERT, Constants.KEY_ID_AUTH)

        WalletApi.isLock = false

        val ownerDIDDoc = addProofsToDocument(
            deviceDIDDoc,
            keyIds,
            did,
            Constants.DID_DOC_TYPE_DEVICE,
            "",
            false
        ) as DIDDocument

        val attDIDDoc = getWalletAttestedDIDDoc(walletUrl, ownerDIDDoc.toJson()).get()
        val attestedDIDDoc = MessageUtil.deserialize(attDIDDoc, AttestedDidDoc::class.java)

        context?.let { ctx ->
            attestedDIDDoc.walletId?.let { walletId ->
                Preference.saveWalletId(ctx, walletId)
            } ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "attestedDIDDoc.walletId"
            )
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "context"
        )

        P131RequestVo(WalletUtil.createMessageId()).apply {
            this.attestedDidDoc = attestedDIDDoc
        }.let { requestVo ->
            if (requestRegisterWallet(tasUrl, requestVo.toJson()).get() != null) {
                walletCore.saveDocument(Constants.DID_DOC_TYPE_DEVICE)
            }
        }

        WalletApi.isLock = true
    }

    private fun getWalletAttestedDIDDoc(
        walletUrl: String,
        request: String
    ): CompletableFuture<String> {
        val api = "/wallet/api/v1/request-sign-wallet"
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(walletUrl + api, "POST", request)
            }.onFailure { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }.getOrThrow()
        }
            .thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    private fun requestRegisterWallet(tasUrl: String, request: String): CompletableFuture<String> {
        val api = "/tas/api/v1/request-register-wallet"
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(tasUrl + api, "POST", request)
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }
            .thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    override fun bindUser(): Boolean {
        return context?.let { ctx ->
            DBManager.getDatabases(ctx).let { walletDB ->
                walletDB.tokenDao()?.getAll()?.get(0)?.pii?.let { pii ->
                    Preference.savePii(ctx, pii)

                    walletDB.userDao()?.also { userDao ->
                        userDao.insertAll(User(pii))
                    }
                    true
                } ?: false
            }
        } ?: false
    }

    override fun unbindUser(): Boolean {
        return context?.let { ctx ->
            DBManager.getDatabases(ctx).let { walletDB ->
                walletDB.userDao()?.run {
                    deleteAll()
                    true
                } ?: false
            }
        } ?: false
    }

    @Throws(WalletCoreException::class)
    override fun deleteWallet() {
        Thread {
            context?.let { ctx ->
                DBManager.getDatabases(ctx).let { walletDB ->
                    walletDB.caPkgDao()?.deleteAll()
                    walletDB.userDao()?.deleteAll()
                    walletDB.tokenDao()?.deleteAll()
                }
            }
        }.start()

        walletCore.deleteWallet()
    }

    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    override fun createHolderDIDDoc(): DIDDocument {
        return walletCore.createHolderDIDDoc().also {
            walletCore.saveDocument(Constants.DID_DOC_TYPE_HOLDER)
        }
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    override fun createSignedDIDDoc(ownerDIDDoc: DIDDocument): SignedDidDoc {
        ownerDIDDoc ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "ownerDIDDoc"
        )

        return SignedDidDoc().apply {
            // Set owner DID doc
            this.ownerDidDoc = MultibaseUtils.encode(
                MultibaseType.MULTIBASE_TYPE.BASE_64,
                ownerDIDDoc.toJson().toByteArray()
            )

            // Set wallet
            this.wallet = Wallet().apply {
                this.did = walletCore.getDocument(Constants.DID_DOC_TYPE_DEVICE)?.id
                    ?: throw WalletException(
                        WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                        "document id"
                    )

                this.id = context?.let { ctx ->
                    Preference.loadWalletId(ctx)
                } ?: throw WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "context"
                )
            }

            // Set nonce
            this.nonce = MultibaseUtils.encode(
                MultibaseType.MULTIBASE_TYPE.BASE_64,
                CryptoUtils.generateNonce(16)
            )
        }.let { signedDIDDoc ->
            // Add proofs
            val keyIds = listOf(Constants.KEY_ID_ASSERT)

            val deviceDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_DEVICE)
            val deviceId = deviceDoc?.id ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "document id"
            )

            addProofsToDocument(
                signedDIDDoc,
                keyIds,
                deviceId,
                Constants.DID_DOC_TYPE_DEVICE,
                "",
                false
            ) as SignedDidDoc
        }
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    override fun getSignedWalletInfo(): SignedWalletInfo {
        val deviceDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_DEVICE)
        val deviceId = deviceDoc?.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "document id"
        )

        val walletId = context?.let { ctx ->
            Preference.loadWalletId(ctx)
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "context"
        )

        return SignedWalletInfo().apply {
            // Set wallet
            this.wallet = Wallet().apply {
                this.did = deviceId
                this.id = walletId
            }

            // Set nonce
            this.nonce = MultibaseUtils.encode(
                MultibaseType.MULTIBASE_TYPE.BASE_58_BTC,
                CryptoUtils.generateNonce(16)
            )
        }.let { signedWalletInfo ->
            // Add proofs
            addProofsToDocument(
                signedWalletInfo,
                listOf("assert"),
                deviceId,
                Constants.DID_DOC_TYPE_DEVICE,
                "",
                false
            ) as SignedWalletInfo
        }
    }

    override fun requestRegisterUser(
        tasUrl: String,
        txId: String,
        serverToken: String,
        signedDIDDoc: SignedDidDoc
    ): CompletableFuture<String> {
        return context?.let { ctx ->
            RegUser(ctx).registerUser(tasUrl, txId, serverToken, signedDIDDoc)
        } ?: run {
            val future = CompletableFuture<String>()
            future.completeExceptionally(
                WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "context"
                )
            )
            future
        }
    }

    @Throws(WalletException::class, ExecutionException::class, InterruptedException::class)
    override fun requestRestoreUser(
        tasUrl: String,
        serverToken: String,
        signedDIDAuth: DIDAuth,
        txId: String
    ): CompletableFuture<String> {
        return context?.let { ctx ->
            RestoreUser(ctx).restoreUser(tasUrl, txId, serverToken, signedDIDAuth).get()
                ?.let { result ->
                    CompletableFuture.completedFuture(result)
                } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_RESTORE_USER_FAIL)
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "context"
        )
    }

    @Throws(WalletException::class, ExecutionException::class, InterruptedException::class)
    override fun requestUpdateUser(
        tasUrl: String,
        serverToken: String,
        signedDIDAuth: DIDAuth,
        txId: String
    ): CompletableFuture<String> {
        return context?.let { ctx ->
            UpdateUser(ctx).updateUser(tasUrl, txId, serverToken, signedDIDAuth).get()
                ?.let { result ->
                    CompletableFuture.completedFuture(result)
                } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_UPDATE_USER_FAIL)
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "context"
        )
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    override fun getSignedDIDAuth(authNonce: String, pin: String?): DIDAuth {
        val holderDIDDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
            ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "holderDIDDoc"
            )

        val holderId = holderDIDDoc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "holder id"
        )

        return DIDAuth().apply {
            this.did = holderId
            this.authNonce = authNonce
        }.let { didAuth ->
            if (pin != null) {
                addProofsToDocument(
                    didAuth,
                    listOf(Constants.KEY_ID_PIN),
                    holderId,
                    Constants.DID_DOC_TYPE_HOLDER,
                    pin,
                    true
                ) as DIDAuth
            } else {
                addProofsToDocument(
                    didAuth,
                    listOf(Constants.KEY_ID_BIO),
                    holderId,
                    Constants.DID_DOC_TYPE_HOLDER,
                    "",
                    true
                ) as DIDAuth
            }
        }
    }

    @Throws(
        WalletException::class,
        WalletCoreException::class,
        UtilityException::class,
        ExecutionException::class,
        InterruptedException::class
    )
    override fun requestIssueVc(
        tasUrl: String,
        apiGateWayUrl: String,
        serverToken: String,
        refId: String,
        profile: IssueProfile,
        signedDIDAuth: DIDAuth,
        txId: String
    ): CompletableFuture<String> {
        profile.profile?.let { prof ->
            prof.issuer.did?.let { issuerDid ->
                prof.issuer.certVcRef?.let { certVcRef ->
                    verifyCertVc(
                        RoleType.ROLE_TYPE.ISSUER,
                        issuerDid,
                        certVcRef,
                        apiGateWayUrl
                    )
                } ?: throw WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "certVcRef"
                )
            } ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "issuer.did"
            )
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "profile.profile"
        )

        // TODO: profile 서명 검증

        val holderDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
            ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "holderDoc"
            )

        val holderId = holderDoc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "holder id"
        )

        profile.profile?.process?.reqE2e?.let { reqE2e ->
            reqE2e.publicKey?.let { serverPublicKey ->
                val e2eKeyPair = CryptoUtils.generateECKeyPair(EcType.EC_TYPE.SECP256_R1)
                val iv = CryptoUtils.generateNonce(16)
                val clientPublicKey = MultibaseUtils.decode(e2eKeyPair.publicKey
                    ?: throw WalletException(
                        WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                        "e2eKeyPair.publicKey"
                    ))

                val signedAccE2e = AccE2e().apply {
                    this.iv = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, iv)
                    this.publicKey =
                        MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, clientPublicKey)
                }.let { accE2e ->
                    addProofsToDocument(
                        accE2e,
                        listOf(Constants.KEY_ID_KEY_AGREE),
                        holderId,
                        Constants.DID_DOC_TYPE_HOLDER,
                        "",
                        false
                    ) as AccE2e
                }

                e2eKeyPair.privateKey?.let { privateKey ->
                    val secretKey = CryptoUtils.generateSharedSecret(
                        EcType.EC_TYPE.SECP256_R1,
                        MultibaseUtils.decode(privateKey),
                        MultibaseUtils.decode(serverPublicKey)
                    )

                    reqE2e.nonce?.let { nonce ->
                        val serverNonce = MultibaseUtils.decode(nonce)
                        val e2eKey = mergeSharedSecretAndNonce(
                            secretKey,
                            serverNonce,
                            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256CBC
                        )

                        val reqVc = ReqVC().apply {
                            this.refId = refId
                            this.profile = ReqVC.Profile(
                                profile.id,
                                profile.profile?.process?.issuerNonce
                            )
                        }

                        reqE2e.cipher?.let { cipher ->
                            reqE2e.padding?.let { padding ->
                                val cipherParts = cipher.value.split("-")
                                val info = CipherInfo(
                                    CipherInfo.ENCRYPTION_TYPE.fromValue(cipherParts[0]),
                                    CipherInfo.ENCRYPTION_MODE.fromValue(cipherParts[2]),
                                    CipherInfo.SYMMETRIC_KEY_SIZE.fromValue(cipherParts[1]),
                                    CipherInfo.SYMMETRIC_PADDING_TYPE.fromKey(padding.value)
                                )

                                val encReqVc = CryptoUtils.encrypt(
                                    reqVc.toJson().toByteArray(),
                                    info,
                                    e2eKey,
                                    iv
                                )

                                val encReqVcStr = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, encReqVc)

                                context?.let { ctx ->
                                    val result = IssueVc(ctx).issueVc(
                                        tasUrl, txId, serverToken, signedDIDAuth, signedAccE2e, encReqVcStr
                                    ).get() ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_ISSUE_CREDENTIAL_FAIL)

                                    val vc = decryptVc(result, reqE2e, e2eKeyPair, apiGateWayUrl)

                                    return CompletableFuture.completedFuture(saveVc(vc, apiGateWayUrl))
                                } ?: throw WalletException(
                                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                                    "context"
                                )
                            } ?: throw WalletException(
                                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                                "reqE2e.padding"
                            )
                        } ?: throw WalletException(
                            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                            "reqE2e.cipher"
                        )
                    } ?: throw WalletException(
                        WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                        "reqE2e.nonce"
                    )
                } ?: throw WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "e2eKeyPair.privateKey"
                )
            } ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "reqE2e.publicKey"
            )
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "profile.profile.process.reqE2e"
        )
    }

    @Throws(
        WalletException::class,
        WalletCoreException::class,
        UtilityException::class,
        ExecutionException::class,
        InterruptedException::class
    )
    override fun requestRevokeVc(
        tasUrl: String,
        serverToken: String,
        txId: String,
        vcId: String,
        issuerNonce: String,
        passcode: String,
        authType: VerifyAuthType.VERIFY_AUTH_TYPE
    ): CompletableFuture<String> {
        val holderDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
            ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "holderDoc"
            )

        val holderId = holderDoc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "holder id"
        )

        val signedReqRevokeVc = ReqRevokeVC().apply {
            this.vcId = vcId
            this.issuerNonce = issuerNonce
        }.let { reqRevokeVc ->
            if (authType == VerifyAuthType.VERIFY_AUTH_TYPE.PIN
                || authType == VerifyAuthType.VERIFY_AUTH_TYPE.BIO
                || authType == VerifyAuthType.VERIFY_AUTH_TYPE.PIN_OR_BIO
            ) {
                if (passcode.isNotEmpty()) {
                    addProofsToDocument(
                        reqRevokeVc,
                        listOf(Constants.KEY_ID_PIN),
                        holderId,
                        Constants.DID_DOC_TYPE_HOLDER,
                        passcode,
                        false
                    ) as ReqRevokeVC
                } else {
                    addProofsToDocument(
                        reqRevokeVc,
                        listOf(Constants.KEY_ID_BIO),
                        holderId,
                        Constants.DID_DOC_TYPE_HOLDER,
                        "",
                        false
                    ) as ReqRevokeVC
                }
            } else {
                reqRevokeVc
            }
        }

        return context?.let { ctx ->
            val result = RevokeVc(ctx).revokeVc(tasUrl, txId, serverToken, signedReqRevokeVc).get()
            val responseVo = MessageUtil.deserialize(result, P220ResponseVo::class.java)

            CompletableFuture.completedFuture(result)
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "context"
        )
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    override fun addProofsToDocument(
        document: ProofContainer,
        keyIds: List<String>,
        did: String,
        type: Int,
        passcode: String?,
        isDIDAuth: Boolean
    ): ProofContainer {
        val proofs = ArrayList<Proof>()

        for (keyId in keyIds) {
            val proof = when (keyId) {
                Constants.KEY_ID_ASSERT,
                Constants.KEY_ID_PIN,
                Constants.KEY_ID_BIO -> {
                    if (isDIDAuth) {
                        createProof(did, ProofPurpose.PROOF_PURPOSE.authentication, keyId)
                    } else {
                        createProof(did, ProofPurpose.PROOF_PURPOSE.assertionMethod, keyId)
                    }
                }

                Constants.KEY_ID_AUTH -> {
                    createProof(did, ProofPurpose.PROOF_PURPOSE.authentication, keyId)
                }

                Constants.KEY_ID_KEY_AGREE -> {
                    createProof(did, ProofPurpose.PROOF_PURPOSE.keyAgreement, keyId)
                }

                else -> throw WalletException(WalletErrorCode.ERR_CODE_WALLET_CREATE_PROOF_FAIL)
            }

            document.setProof(proof)

            val signature = if (keyId == Constants.KEY_ID_PIN && passcode != null) {
                walletCore.sign(
                    keyId,
                    passcode.toByteArray(),
                    document.toJson().toByteArray(),
                    type
                )
            } else {
                walletCore.sign(keyId, "".toByteArray(), document.toJson().toByteArray(), type)
            }

            signature ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_CREATE_PROOF_FAIL)

            proof.apply {
                proofValue = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, signature)
            }.also {
                document.setProof(it)
            }

            if (keyIds.size == 1) {
                return document
            }

            proofs.add(proof)
            document.setProof(null)
        }

        document.setProofs(proofs)
        return document
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    override fun createEncVp(
        vcId: String,
        claimCode: List<String>,
        reqE2e: ReqE2e,
        passcode: String,
        nonce: String,
        authType: VerifyAuthType.VERIFY_AUTH_TYPE
    ): ReturnEncVP {
        val holderDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
            ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "holderDoc"
            )

        val holderId = holderDoc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "holder id"
        )

        reqE2e.publicKey?.let { serverPublicKey ->
            val e2eKeyPair = CryptoUtils.generateECKeyPair(EcType.EC_TYPE.SECP256_R1)
            val iv = CryptoUtils.generateNonce(16)

            e2eKeyPair.publicKey?.let { clientPublicKey ->
                val signedAccE2e = AccE2e().apply {
                    this.iv = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, iv)
                    this.publicKey =
                        MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, MultibaseUtils.decode(clientPublicKey))
                }.let { accE2e ->
                    addProofsToDocument(
                        accE2e,
                        listOf(Constants.KEY_ID_KEY_AGREE),
                        holderId,
                        Constants.DID_DOC_TYPE_HOLDER,
                        "",
                        false
                    ) as AccE2e
                }

                e2eKeyPair.privateKey?.let { privateKey ->
                    val secretKey = CryptoUtils.generateSharedSecret(
                        EcType.EC_TYPE.SECP256_R1,
                        MultibaseUtils.decode(privateKey),
                        MultibaseUtils.decode(serverPublicKey)
                    )

                    reqE2e.nonce?.let { reqNonce ->
                        val serverNonce = MultibaseUtils.decode(reqNonce)
                        val e2eKey = mergeSharedSecretAndNonce(
                            secretKey,
                            serverNonce,
                            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256CBC
                        )

                        reqE2e.cipher?.let { cipher ->
                            reqE2e.padding?.let { padding ->
                                val cipherParts = cipher.value.split("-")
                                val info = CipherInfo(
                                    CipherInfo.ENCRYPTION_TYPE.fromValue(cipherParts[0]),
                                    CipherInfo.ENCRYPTION_MODE.fromValue(cipherParts[2]),
                                    CipherInfo.SYMMETRIC_KEY_SIZE.fromValue(cipherParts[1]),
                                    CipherInfo.SYMMETRIC_PADDING_TYPE.fromKey(padding.value)
                                )

                                val claimInfos = ArrayList<ClaimInfo>().apply {
                                    add(ClaimInfo(vcId, claimCode))
                                }

                                val vp = PresentationInfo().apply {
                                    holder = holderId // holder did
                                    validFrom = WalletUtil.getDate()
                                    validUntil = WalletUtil.createValidUntil(600)
                                    verifierNonce = nonce
                                }.let { presentationInfo ->
                                    walletCore.makePresentation(claimInfos, presentationInfo)
                                }

                                val signedVp = if (authType == VerifyAuthType.VERIFY_AUTH_TYPE.PIN
                                    || authType == VerifyAuthType.VERIFY_AUTH_TYPE.BIO
                                    || authType == VerifyAuthType.VERIFY_AUTH_TYPE.PIN_OR_BIO
                                ) {
                                    if (passcode.isNotEmpty()) {
                                        addProofsToDocument(
                                            vp,
                                            listOf(Constants.KEY_ID_PIN),
                                            holderId,
                                            Constants.DID_DOC_TYPE_HOLDER,
                                            passcode,
                                            false
                                        ) as VerifiablePresentation
                                    } else {
                                        addProofsToDocument(
                                            vp,
                                            listOf(Constants.KEY_ID_BIO),
                                            holderId,
                                            Constants.DID_DOC_TYPE_HOLDER,
                                            "",
                                            false
                                        ) as VerifiablePresentation
                                    }
                                } else {
                                    vp
                                }

                                val encVp = CryptoUtils.encrypt(
                                    signedVp.toJson().toByteArray(),
                                    info,
                                    e2eKey,
                                    iv
                                )

                                val encVpStr = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, encVp)

                                return ReturnEncVP().apply {
                                    this.encVp = encVpStr
                                    this.accE2e = signedAccE2e
                                }
                            } ?: throw WalletException(
                                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                                "reqE2e.padding"
                            )
                        } ?: throw WalletException(
                            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                            "reqE2e.cipher"
                        )
                    } ?: throw WalletException(
                        WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                        "reqE2e.nonce"
                    )
                } ?: throw WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "e2eKeyPair.privateKey"
                )
            } ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "e2eKeyPair.publicKey"
            )
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "reqE2e.publicKey"
        )
    }

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class, ExecutionException::class, InterruptedException::class)
    private fun verifyCertVc(
        roleType: RoleType.ROLE_TYPE,
        providerDID: String,
        providerURL: String,
        apiGateWayUrl: String
    ) {
        val certVcStr = WalletUtil.getCertVc(providerURL).get()
        val certVc = MessageUtil.deserialize(certVcStr, VerifiableCredential::class.java)

        certVc.credentialSubject?.let { subject ->
            if (subject.id != providerDID)
                throw WalletException(WalletErrorCode.ERR_CODE_WALLET_DID_MATCH_FAIL)

            certVc.credentialSchema?.let { schema ->
                schema.id?.let { schemaUrl ->
                    val schemaData = WalletUtil.getVcSchema(schemaUrl).get()
                    val vcSchema = MessageUtil.deserialize(schemaData, VCSchema::class.java)

                    vcSchema.credentialSubject?.let { schemaCred ->
                        val vcSchemaClaims = schemaCred.claims
                        val certVcClaims = subject.claims

                        var isExistRole = false
                        vcSchemaClaims?.forEach { schemaClaim ->
                            schemaClaim.items?.forEach { item ->
                                if (item.caption == "role") {
                                    certVcClaims?.forEach { certVcClaim ->
                                        if (certVcClaim.caption == item.caption) {
                                            if (roleType.value == certVcClaim.value)
                                                isExistRole = true
                                        }
                                    }
                                }
                            }
                        }

                        if (!isExistRole)
                            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_ROLE_MATCH_FAIL)

                        if (!verifyProof(certVcStr, false, apiGateWayUrl))
                            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_CERT_VC_FAIL)
                    } ?: throw WalletException(
                        WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                        "vcSchema.credentialSubject"
                    )
                } ?: throw WalletException(
                    WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                    "credentialSchema.id"
                )
            } ?: throw WalletException(
                WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
                "credentialSchema"
            )
        } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "credentialSubject"
        )
    }

    @Throws(UtilityException::class)
    private fun mergeSharedSecretAndNonce(
        sharedSecret: ByteArray,
        nonce: ByteArray,
        cipherType: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE
    ): ByteArray {
        val length = sharedSecret.size + nonce.size
        val digest = ByteArray(length).also { combined ->
            System.arraycopy(sharedSecret, 0, combined, 0, sharedSecret.size)
            System.arraycopy(nonce, 0, combined, sharedSecret.size, nonce.size)
        }

        val combinedResult = DigestUtils.getDigest(digest, DigestEnum.DIGEST_ENUM.SHA_256)

        return when {
            cipherType == SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES128CBC ||
                    cipherType == SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES128ECB -> {
                ByteArray(16).apply {
                    System.arraycopy(combinedResult, 0, this, 0, 16)
                }
            }
            cipherType == SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256CBC ||
                    cipherType == SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256ECB -> {
                ByteArray(32).apply {
                    System.arraycopy(combinedResult, 0, this, 0, 32)
                }
            }
            else -> ByteArray(0)
        }
    }

    @Throws(UtilityException::class)
    private fun decryptVc(e2e: String, reqE2e: ReqE2e, dhKeyPair: EcKeyPair, apiGateWayUrl: String): String {
        return MessageUtil.deserialize(e2e, P210ResponseVo::class.java).let { p210ResponseVo ->
            p210ResponseVo.e2e?.let { e2eData ->
                e2eData.iv?.let { iv ->
                    e2eData.encVc?.let { encVc ->
                        reqE2e.cipher?.let { cipher ->
                            reqE2e.padding?.let { padding ->
                                val cipherParts = cipher.value.split("-")
                                val info = CipherInfo(
                                    CipherInfo.ENCRYPTION_TYPE.fromValue(cipherParts[0]),
                                    CipherInfo.ENCRYPTION_MODE.fromValue(cipherParts[2]),
                                    CipherInfo.SYMMETRIC_KEY_SIZE.fromValue(cipherParts[1]),
                                    CipherInfo.SYMMETRIC_PADDING_TYPE.fromKey(padding.value)
                                )

                                val sessKey = createDhSecret(reqE2e, dhKeyPair)

                                val plain = CryptoUtils.decrypt(
                                    MultibaseUtils.decode(encVc),
                                    info,
                                    sessKey,
                                    MultibaseUtils.decode(iv)
                                )

                                String(plain)
                            } ?: throw UtilityException("reqE2e.padding is null")
                        } ?: throw UtilityException("reqE2e.cipher is null")
                    } ?: throw UtilityException("e2e.encVc is null")
                } ?: throw UtilityException("e2e.iv is null")
            } ?: throw UtilityException("p210ResponseVo.e2e is null")
        }
    }

    private fun createProof(did: String, proofPurpose: ProofPurpose.PROOF_PURPOSE, keyId: String): Proof {
        return Proof().apply {
            created = WalletUtil.getDate()
            this.proofPurpose = proofPurpose
            type = ProofType.PROOF_TYPE.secp256r1Signature2018
            verificationMethod = "$did?versionId=1#$keyId"
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class, ExecutionException::class, InterruptedException::class)
    private fun verifyProof(vcStr: String, isCertVc: Boolean, apiGateWayUrl: String): Boolean {
        // 1. Deserialize VC
        val vc = MessageUtil.deserialize(vcStr, VerifiableCredential::class.java)

        // 2. Determine DID based on whether it's a cert VC
        val did = if (isCertVc) {
            vc.credentialSubject?.id
        } else {
            vc.issuer?.id
        } ?: return false

        // 3. Get DID document
        val didDoc = WalletUtil.getDidDoc(apiGateWayUrl, did).get()
        val didDocVo = MessageUtil.deserialize(didDoc, DidDocVo::class.java)
        val encodedDidDoc = didDocVo.didDoc ?: return false

        // 4. Decode DID document
        val decodedDidDoc = String(MultibaseUtils.decode(encodedDidDoc))
        val didDocument = MessageUtil.deserialize(decodedDidDoc, DIDDocument::class.java)

        // 5. Find public key
        var encodePubKey = ""
        didDocument.verificationMethod?.forEach { vm ->
            if (vm.id == "assert") {
                vm.publicKeyMultibase?.let { pubKey ->
                    encodePubKey = pubKey
                }
            }
        }

        if (encodePubKey.isEmpty()) {
            return false
        }

        // 6. Get proof and signature
        val proof = vc.proof ?: return false
        val proofValue = proof.proofValue ?: return false
        val signature = MultibaseUtils.decode(proofValue)

        // 7. Modify proof for verification
        proof.proofValue = null
        proof.proofValueList = null

        // 8. Verify signature
        val result = walletCore.verify(
            MultibaseUtils.decode(encodePubKey),
            vc.toJson().toByteArray(StandardCharsets.UTF_8),
            signature
        )

        WalletLogger.d("verify verifyProof : $result")

        return result ?: false
    }

    @Throws(UtilityException::class)
    private fun createDhSecret(reqE2e: ReqE2e, dhKeyPair: EcKeyPair): ByteArray {
        reqE2e.publicKey?.let { multibasePubKey ->
            dhKeyPair.privateKey?.let { privKeyBase ->
                val privKey = MultibaseUtils.decode(privKeyBase)
                val serverPubKey = MultibaseUtils.decode(multibasePubKey)

                val secretKey = CryptoUtils.generateSharedSecret(
                    EcType.EC_TYPE.SECP256_R1,
                    privKey,
                    serverPubKey
                )

                reqE2e.nonce?.let { nonceBase ->
                    val serverNonce = MultibaseUtils.decode(nonceBase)

                    return mergeSharedSecretAndNonce(
                        secretKey,
                        serverNonce,
                        SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256CBC
                    )
                } ?: throw UtilityException("reqE2e.nonce is null")
            } ?: throw UtilityException("dhKeyPair.privateKey is null")
        } ?: throw UtilityException("reqE2e.publicKey is null")
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class, ExecutionException::class, InterruptedException::class)
    private fun saveVc(vcStr: String, apiGateWayUrl: String): String {
        val removeIds = ArrayList<String>()
        val vc = MessageUtil.deserialize(vcStr, VerifiableCredential::class.java)

        vc.id ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "vc.id"
        )

        if (walletCore.isAnyCredentialsSaved()) {
            walletCore.getAllCredentials()?.forEach { verifiableCredential ->
                vc.credentialSchema?.id?.let { schemaId ->
                    verifiableCredential.credentialSchema?.id?.let { vcSchemaId ->
                        if (schemaId == vcSchemaId) {
                            verifiableCredential.id?.let { id ->
                                removeIds.add(id)
                            }
                        }
                    }
                }
            }

            if (removeIds.size > 0) {
                walletCore.deleteCredentials(removeIds)
            }
        }

        if (verifyProof(vcStr, false, apiGateWayUrl)) {
            walletCore.addCredentials(vc)
        }

        return vc.id
    }
}