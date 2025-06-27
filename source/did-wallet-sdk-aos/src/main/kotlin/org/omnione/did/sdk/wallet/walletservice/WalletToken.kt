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

package org.omnione.did.sdk.wallet.walletservice

import android.content.Context
import org.omnione.did.sdk.datamodel.did.DidDocVo
import org.omnione.did.sdk.datamodel.common.enums.RoleType
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.util.MessageUtil
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import org.omnione.did.sdk.datamodel.vcschema.VCSchema
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.Encodings.Base16
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.utility.MultibaseUtils
import org.omnione.did.sdk.wallet.WalletCore
import org.omnione.did.sdk.wallet.walletservice.db.DBManager
import org.omnione.did.sdk.wallet.walletservice.db.Preference
import org.omnione.did.sdk.wallet.walletservice.db.Token
import org.omnione.did.sdk.datamodel.token.WalletTokenData
import org.omnione.did.sdk.datamodel.common.enums.WalletTokenPurpose
import org.omnione.did.sdk.datamodel.token.WalletTokenSeed
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.wallet.walletservice.exception.WalletErrorCode
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.wallet.walletservice.logger.WalletLogger
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.wallet.walletservice.util.WalletUtil
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class WalletToken {
    private var context: Context? = null
    private var walletCore: WalletCore? = null
    private var walletLogger: WalletLogger? = null

    constructor()

    constructor(context: Context, walletCore: WalletCore) {
        this.context = context
        this.walletCore = walletCore
        walletLogger = WalletLogger.getInstance()
    }

    @Throws(WalletException::class)
    fun verifyWalletToken(hWalletToken: String?, purposeList: List<WalletTokenPurpose.WALLET_TOKEN_PURPOSE>) {
        context?.let { ctx ->
            DBManager.getDatabases(ctx).run {
                tokenDao()?.let { tokenDao ->
                    if (tokenDao.getAll().isEmpty()) {
                        throw WalletException(WalletErrorCode.ERR_CODE_WALLET_SELECT_QUERY_FAIL)
                    }

                    tokenDao.getAll()[0].let { token ->
                        val hWalletToken_db = token.hWalletToken
                        val validUntil_db = token.validUntil
                        val purpose_db = token.purpose

                        WalletLogger.d("verifyWalletToken 인가앱 hwallettoken: $hWalletToken")
                        WalletLogger.d("verifyWalletToken walletToken db: $hWalletToken_db")
                        WalletLogger.d("db: $validUntil_db / $purpose_db")

                        if (hWalletToken != hWalletToken_db) {
                            WalletLogger.e("walletToken 검증 실패")
                            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_TOKEN_FAIL)
                        }

                        validUntil_db?.let { validUntil ->
                            if (!WalletUtil.checkDate(validUntil)) {
                                WalletLogger.e("valid until 검증 실패")
                                throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_TOKEN_FAIL)
                            }
                        }

                        purposeList.any { it.toString() == purpose_db }.takeIf { it }
                            ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_TOKEN_FAIL)

                        WalletLogger.d("walletToken 검증 성공")
                        WalletLogger.d("valid until 검증 성공")
                        WalletLogger.d("purpose 검증 성공")
                    }
                }
            }
        }
    }

    @Throws(UtilityException::class, WalletException::class)
    fun createWalletTokenSeed(
        purpose: WalletTokenPurpose.WALLET_TOKEN_PURPOSE?,
        pkgName: String,
        userId: String
    ): WalletTokenSeed {
        purpose ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "purpose")

        pkgName.takeIf { it.isNotEmpty() }
            ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "pkgName")

        userId.takeIf { it.isNotEmpty() }
            ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "userId")

        thread {
            deleteTokenData()
        }

        return WalletTokenSeed().apply {
            this.purpose = purpose
            this.pkgName = pkgName
            this.userId = userId
            this.nonce = CryptoUtils.generateNonce(16).let { nonce ->
                MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER, nonce)
            }
            this.validUntil = WalletUtil.createValidUntil(30) // set valid until
        }
    }

    @Throws(UtilityException::class, WalletException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun createNonceForWalletToken(
        apiGateWayUrl: String,
        walletTokenData: WalletTokenData?
    ): String {
        apiGateWayUrl.takeIf { it.isNotEmpty() }
            ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "apiGateWayUrl")

        walletTokenData ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "walletTokenData"
        )

        walletTokenData.provider?.let { provider ->
            provider.getDID()?.let { providerDID ->
                provider.certVcRef?.let { certVcRef ->
                    verifyCertVc(
                        RoleType.ROLE_TYPE.APP_PROVIDER,
                        providerDID,
                        certVcRef,
                        apiGateWayUrl
                    )
                } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "certVcRef")
            } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "provider DID")
        } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "provider")

        if (!verifyWalletTokenDataProof(walletTokenData, apiGateWayUrl))
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_CREATE_PROOF_FAIL)

        return CryptoUtils.generateNonce(16).let { nonce ->
            MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER, nonce)
        }.also { resultNonce ->
            createWalletToken(walletTokenData, resultNonce).let { hWalletToken ->
                Executors.newSingleThreadExecutor().let { executor ->
                    executor.submit(Callable {
                        insertTokenData(walletTokenData, hWalletToken)
                        null
                    }).get()
                    executor.shutdown()
                }
            }
            WalletLogger.d("resultNonce : $resultNonce")
        }
    }

    @Throws(UtilityException::class)
    private fun createWalletToken(walletTokenData: WalletTokenData, resultNonce: String): String {
        return (walletTokenData.toJson() + resultNonce).let { walletToken ->
            DigestUtils.getDigest(
                walletToken.toByteArray(),
                DigestEnum.DIGEST_ENUM.SHA_256
            ).let { digest ->
                Base16.toHex(digest)
            }
        }.also { walletToken ->
            WalletLogger.d("wallet token : $walletToken")
        }
    }

    private fun insertTokenData(walletTokenData: WalletTokenData, hWalletToken: String) {
        context?.let { ctx ->
            DBManager.getDatabases(ctx).tokenDao()?.let { tokenDao ->
                walletTokenData.seed?.let { seed ->
                    Token().apply {
                        walletId = Preference.loadWalletId(ctx)
                        validUntil = seed.validUntil
                        purpose = seed.purpose.toString()
                        pii = walletTokenData.sha256_pii
                        nonce = seed.nonce
                        pkgName = seed.pkgName
                        this.hWalletToken = hWalletToken
                        createDate = WalletUtil.getDate()
                    }.let { token ->
                        tokenDao.insertAll(token)
                    }
                } ?: WalletLogger.e("seed is null in walletTokenData")
            }
        }
    }

    private fun deleteTokenData() {
        context?.let { ctx ->
            DBManager.getDatabases(ctx).tokenDao()?.let { tokenDao ->
                if (tokenDao.getAll().isNotEmpty()) {
                    tokenDao.getAll()[0].let { token ->
                        tokenDao.deleteByIdx(token.idx)
                    }
                }
            }
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, ExecutionException::class, InterruptedException::class, WalletException::class)
    private fun verifyCertVc(
        roleType: RoleType.ROLE_TYPE,
        providerDID: String,
        providerURL: String,
        apiGateWayUrl: String
    ) {
        WalletUtil.getCertVc(providerURL).get().let { certVcStr ->
            MessageUtil.deserialize(certVcStr, VerifiableCredential::class.java).let { certVc ->
                certVc.credentialSubject?.let { subject ->
                    if (subject.id != providerDID)
                        throw WalletException(WalletErrorCode.ERR_CODE_WALLET_DID_MATCH_FAIL)

                    certVc.credentialSchema?.let { schema ->
                        schema.id?.let { schemaUrl ->
                            WalletUtil.getVcSchema(schemaUrl).get().let { schemaData ->
                                MessageUtil.deserialize(schemaData, VCSchema::class.java).let { vcSchema ->
                                    vcSchema.credentialSubject?.let { schemaCred ->
                                        val vcSchemaClaims = schemaCred.getClaims()
                                        val certVcClaims = subject.claims

                                        var isExistRole = false
                                        vcSchemaClaims?.forEach { schemaClaim ->
                                            schemaClaim.items?.forEach { item ->
                                                if (item.caption == "role") {
                                                    certVcClaims?.forEach { certVcClaim ->
                                                        if (certVcClaim.caption == item.caption && roleType.value == certVcClaim.value) {
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
                                    } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "vcSchema.credentialSubject")
                                }
                            }
                        } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "credentialSchema.id")
                    } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "credentialSchema")
                } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL, "credentialSubject")
            }
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class, ExecutionException::class, InterruptedException::class)
    private fun verifyProof(vcStr: String, isCertVc: Boolean, apiGateWayUrl: String): Boolean {
        val vc = MessageUtil.deserialize(vcStr, VerifiableCredential::class.java)

        val did = if (isCertVc) {
            vc.credentialSubject?.id
        } else {
            vc.issuer?.id
        } ?: return false

        val didDoc = WalletUtil.getDidDoc(apiGateWayUrl, did).get()
        val didDocVo = MessageUtil.deserialize(didDoc, DidDocVo::class.java)
        val encodedDidDoc = didDocVo.didDoc ?: return false

        val decodedDidDoc = String(MultibaseUtils.decode(encodedDidDoc))
        val didDocument = MessageUtil.deserialize(decodedDidDoc, DIDDocument::class.java)

        // Find the assertion verification method and get public key
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

        // Get signature and prepare proof
        val proof = vc.proof ?: return false
        val proofValue = proof.proofValue ?: return false
        val signature = MultibaseUtils.decode(proofValue)

        proof.proofValue = null
        proof.proofValueList = null

        // Verify signature
        return walletCore?.verify(
            MultibaseUtils.decode(encodePubKey),
            vc.toJson().toByteArray(StandardCharsets.UTF_8),
            signature
        )?.also { result ->
            WalletLogger.d("verify certVC : $result")
        } ?: false
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class, ExecutionException::class, InterruptedException::class)
    private fun verifyWalletTokenDataProof(walletTokenData: WalletTokenData, apiGateWayUrl: String): Boolean {
        return walletTokenData.provider?.getDID()?.let { did ->
            WalletUtil.getDidDoc(apiGateWayUrl, did).get().let { didDoc ->
                MessageUtil.deserialize(didDoc, DidDocVo::class.java).let { didDocVo ->
                    didDocVo.didDoc?.let { encodedDidDoc ->
                        String(MultibaseUtils.decode(encodedDidDoc)).let { decodedDidDoc ->
                            MessageUtil.deserialize(decodedDidDoc, DIDDocument::class.java).let { didDocument ->
                                // Find the assertion verification method and get public key
                                var encodePubKey = ""
                                didDocument.verificationMethod?.forEach { vm ->
                                    if (vm.id == "assert") {
                                        vm.publicKeyMultibase?.let { pubKey ->
                                            encodePubKey = pubKey
                                        }
                                    }
                                }

                                if (encodePubKey.isEmpty()) {
                                    return@let false
                                }

                                // Get signature and prepare proof
                                walletTokenData.proof?.let { proof ->
                                    proof.proofValue?.let { proofValue ->
                                        val signature = MultibaseUtils.decode(proofValue)
                                        walletTokenData.proof?.let { proof ->
                                            proof.proofValue = null
                                        }

                                        // Verify signature
                                        walletCore?.verify(
                                            MultibaseUtils.decode(encodePubKey),
                                            walletTokenData.toJson().toByteArray(StandardCharsets.UTF_8),
                                            signature
                                        ) ?: false
                                    } ?: false
                                } ?: false
                            }
                        }
                    } ?: false
                }
            }
        } ?: false
    }
}