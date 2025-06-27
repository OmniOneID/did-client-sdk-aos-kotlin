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
import androidx.fragment.app.Fragment
import org.omnione.did.sdk.core.bioprompthelper.BioPromptHelper
import org.omnione.did.sdk.core.didmanager.DIDManager
import org.omnione.did.sdk.core.didmanager.datamodel.DIDKeyInfo
import org.omnione.did.sdk.core.didmanager.datamodel.DIDMethodType
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.keymanager.KeyManager
import org.omnione.did.sdk.core.keymanager.datamodel.*
import org.omnione.did.sdk.core.vcmanager.VCManager
import org.omnione.did.sdk.core.vcmanager.datamodel.ClaimInfo
import org.omnione.did.sdk.core.vcmanager.datamodel.PresentationInfo
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import org.omnione.did.sdk.datamodel.vp.VerifiablePresentation
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.utility.MultibaseUtils
import org.omnione.did.sdk.wallet.walletservice.config.Config
import org.omnione.did.sdk.wallet.walletservice.config.Constants
import org.omnione.did.sdk.wallet.walletservice.exception.WalletErrorCode
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.wallet.walletservice.logger.WalletLogger

class WalletCore : WalletCoreInterface {
    private var context: Context? = null
    private lateinit var deviceKeyManager: KeyManager<DetailKeyInfo>
    private lateinit var deviceDIDManager: DIDManager<DIDDocument>
    private lateinit var keyManager: KeyManager<DetailKeyInfo>
    private lateinit var didManager: DIDManager<DIDDocument>
    private lateinit var vcManager: VCManager<VerifiableCredential>
    private lateinit var bioPromptHelper: BioPromptHelper
    private lateinit var walletLogger: WalletLogger
    private var bioPromptInterface: BioPromptHelper.BioPromptInterface? = null

    interface BioPromptInterface {
        fun onSuccess(result: String)
        fun onFail(result: String)
    }

    fun setBioPromptListener(bioPromptInterface: BioPromptHelper.BioPromptInterface) {
        this.bioPromptInterface = bioPromptInterface
    }

    constructor()

    @Throws(WalletCoreException::class)
    constructor(context: Context) {
        this.context = context
        deviceKeyManager = KeyManager(Constants.WALLET_DEVICE, context)
        deviceDIDManager = DIDManager(Constants.WALLET_DEVICE, context)
        keyManager = KeyManager(Constants.WALLET_HOLDER, context)
        didManager = DIDManager(Constants.WALLET_HOLDER, context)
        vcManager = VCManager(Constants.WALLET_HOLDER, context)
        bioPromptHelper = BioPromptHelper(context)
        walletLogger = WalletLogger.getInstance()
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    override fun createDeviceDIDDoc(): DIDDocument {
        return if (!deviceKeyManager.isAnyKeySaved()) {
            val keyGenWalletMethodType = KeyGenWalletMethodType()

            // Generate ASSERT key
            WalletKeyGenRequest(
                Constants.KEY_ID_ASSERT,
                AlgorithmType.ALGORITHM_TYPE.SECP256R1,
                StorageOption.STORAGE_OPTION.WALLET,
                keyGenWalletMethodType
            ).let { keyGenInfo ->
                deviceKeyManager.generateKey(keyGenInfo)
            }

            // Generate AUTH key
            WalletKeyGenRequest(
                Constants.KEY_ID_AUTH,
                AlgorithmType.ALGORITHM_TYPE.SECP256R1,
                StorageOption.STORAGE_OPTION.WALLET,
                keyGenWalletMethodType
            ).let { keyGenInfo ->
                deviceKeyManager.generateKey(keyGenInfo)
            }

            // Generate KEY_AGREE key
            WalletKeyGenRequest(
                Constants.KEY_ID_KEY_AGREE,
                AlgorithmType.ALGORITHM_TYPE.SECP256R1,
                StorageOption.STORAGE_OPTION.WALLET,
                keyGenWalletMethodType
            ).let { keyGenInfo ->
                deviceKeyManager.generateKey(keyGenInfo)
            }

            val did = DIDManager.genDID(Config.DID_METHOD)
            val controller = Config.DID_CONTROLLER
            val keyInfos = deviceKeyManager.getKeyInfos(listOf(Constants.KEY_ID_ASSERT, Constants.KEY_ID_AUTH, Constants.KEY_ID_KEY_AGREE))

            ArrayList<DIDKeyInfo>().apply {
                keyInfos.forEach { keyInfo ->
                    when (keyInfo.id) {
                        Constants.KEY_ID_ASSERT ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.assertionMethod), controller)
                        Constants.KEY_ID_AUTH ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.authentication), controller)
                        Constants.KEY_ID_KEY_AGREE ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.keyAgreement), controller)
                        else -> DIDKeyInfo()
                    }.let { didKeyInfo ->
                        add(didKeyInfo)
                    }
                }
            }.let { didKeyInfos ->
                deviceDIDManager.createDocument(did, didKeyInfos, controller, null)
                deviceDIDManager.getDocument()
            }
        } else {
            deviceDIDManager.getDocument()
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun createHolderDIDDoc(): DIDDocument {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return if (!keyManager.isKeySaved(Constants.KEY_ID_KEY_AGREE)) {
            val keyGenWalletMethodType = KeyGenWalletMethodType()

            // Generate KEY_AGREE key
            WalletKeyGenRequest(
                Constants.KEY_ID_KEY_AGREE,
                AlgorithmType.ALGORITHM_TYPE.SECP256R1,
                StorageOption.STORAGE_OPTION.WALLET,
                keyGenWalletMethodType
            ).let { keyGenInfo ->
                keyManager.generateKey(keyGenInfo)
            }

            val did = DIDManager.genDID(Config.DID_METHOD)
            val controller = Config.DID_CONTROLLER
            val keyInfos = keyManager.getKeyInfos(listOf(Constants.KEY_ID_PIN, Constants.KEY_ID_BIO, Constants.KEY_ID_KEY_AGREE))

            ArrayList<DIDKeyInfo>().apply {
                keyInfos.forEach { keyInfo ->
                    when (keyInfo.id) {
                        Constants.KEY_ID_PIN ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.assertionMethod, DIDMethodType.DID_METHOD_TYPE.authentication), controller)
                        Constants.KEY_ID_BIO ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.assertionMethod, DIDMethodType.DID_METHOD_TYPE.authentication), controller)
                        Constants.KEY_ID_KEY_AGREE ->
                            DIDKeyInfo(keyInfo, listOf(DIDMethodType.DID_METHOD_TYPE.keyAgreement), controller)
                        else -> DIDKeyInfo()
                    }.let { didKeyInfo ->
                        add(didKeyInfo)
                    }
                }
            }.let { didKeyInfos ->
                didManager.createDocument(did, didKeyInfos, controller, null)
                didManager.getDocument()
            }
        } else {
            didManager.getDocument()
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun generateKeyPair(passcode: String?) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        passcode?.takeIf { it.isNotEmpty() } ?: throw WalletException(
            WalletErrorCode.ERR_CODE_WALLET_VERIFY_PARAMETER_FAIL,
            "passcode"
        )

        WalletKeyGenRequest().apply {
            id = Constants.KEY_ID_PIN
            algorithmType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
            storage = StorageOption.STORAGE_OPTION.WALLET
            methodType = KeyGenWalletMethodType(
                MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, passcode.toByteArray())
            )
        }.let { keyGenRequest ->
            keyManager.generateKey(keyGenRequest)
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun getDocument(type: Int): DIDDocument? {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return when (type) {
            Constants.DID_DOC_TYPE_DEVICE -> deviceDIDManager.getDocument() // device
            Constants.DID_DOC_TYPE_HOLDER -> didManager.getDocument() // holder
            else -> null
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun saveDocument(type: Int) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        when (type) {
            Constants.DID_DOC_TYPE_DEVICE -> deviceDIDManager.saveDocument()
            else -> didManager.saveDocument()
        }
    }

    override fun isExistWallet(): Boolean {
        return deviceKeyManager.isAnyKeySaved().also { keySaved ->
            WalletLogger.d("No registered device key : $keySaved")
        } && deviceDIDManager.isSaved().also { didSaved ->
            WalletLogger.d("No registered device DID : $didSaved")
        }
    }

    @Throws(WalletCoreException::class)
    override fun deleteWallet() {
        with(deviceKeyManager) {
            if (isAnyKeySaved()) deleteAllKeys()
        }

        with(deviceDIDManager) {
            if (isSaved()) deleteDocument()
        }

        with(keyManager) {
            if (isAnyKeySaved()) deleteAllKeys()
        }

        with(didManager) {
            if (isSaved()) deleteDocument()
        }

        with(vcManager) {
            if (isAnyCredentialsSaved()) deleteAllCredentials()
        }
    }

    @Throws(WalletException::class)
    override fun isAnyCredentialsSaved(): Boolean {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return vcManager.isAnyCredentialsSaved()
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun addCredentials(verifiableCredential: VerifiableCredential) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        vcManager.addCredentials(verifiableCredential)
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun getCredentials(identifiers: List<String>): List<VerifiableCredential>? {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return vcManager.takeIf { it.isAnyCredentialsSaved() }?.getCredentials(identifiers)
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun getAllCredentials(): List<VerifiableCredential>? {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return vcManager.takeIf { it.isAnyCredentialsSaved() }?.getAllCredentials()
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun deleteCredentials(identifiers: List<String>) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        vcManager.deleteCredentials(identifiers)
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun makePresentation(claimInfos: List<ClaimInfo>, presentationInfo: PresentationInfo): VerifiablePresentation {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return vcManager.makePresentation(claimInfos, presentationInfo)
    }

    @Throws(WalletException::class)
    override fun registerBioKey(ctx: Context) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        bioPromptHelper.apply {
            setBioPromptListener(object : BioPromptHelper.BioPromptInterface {
                override fun onSuccess(result: String) {
                    runCatching {
                        SecureKeyGenRequest().apply {
                            id = Constants.KEY_ID_BIO
                            algorithmType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
                            storage = StorageOption.STORAGE_OPTION.KEYSTORE
                            accessMethod = KeyStoreAccessMethod.KEYSTORE_ACCESS_METHOD.BIOMETRY
                        }.let { keyGenInfo ->
                            keyManager.generateKey(keyGenInfo)
                        }
                    }.onSuccess {
                        bioPromptInterface?.onSuccess(result)
                    }.onFailure { e ->
                        when (e) {
                            is WalletCoreException, is UtilityException -> throw RuntimeException(e)
                            else -> throw e
                        }
                    }
                }

                override fun onError(result: String) {
                    bioPromptInterface?.onError(result)
                }

                override fun onCancel(result: String) {
                    bioPromptInterface?.onCancel(result)
                }

                override fun onFail(result: String) {
                    bioPromptInterface?.onFail(result)
                }
            })
            registerBioKey(ctx, null)
        }
    }

    @Throws(WalletCoreException::class, WalletException::class)
    override fun authenticateBioKey(fragment: Fragment, ctx: Context) {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        bioPromptHelper.apply {
            setBioPromptListener(object : BioPromptHelper.BioPromptInterface {
                override fun onSuccess(result: String) {
                    bioPromptInterface?.onSuccess(result)
                }

                override fun onError(result: String) {
                    bioPromptInterface?.onError(result)
                }

                override fun onCancel(result: String) {
                    bioPromptInterface?.onCancel(result)
                }

                override fun onFail(result: String) {
                    bioPromptInterface?.onFail(result)
                }
            })
            authenticateBioKey(fragment, ctx, null)
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun sign(id: String, pin: ByteArray, digest: ByteArray, type: Int): ByteArray? {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return DigestUtils.getDigest(digest, DigestEnum.DIGEST_ENUM.SHA_256).let { hashedData ->
            when (type) {
                Constants.DID_DOC_TYPE_DEVICE -> deviceKeyManager.sign(id, pin, hashedData)
                Constants.DID_DOC_TYPE_HOLDER -> keyManager.sign(id, pin, hashedData)
                else -> null
            }
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun verify(publicKey: ByteArray, digest: ByteArray, signature: ByteArray): Boolean {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return DigestUtils.getDigest(digest, DigestEnum.DIGEST_ENUM.SHA_256).let { hashedData ->
            keyManager.verify(AlgorithmType.ALGORITHM_TYPE.SECP256R1, publicKey, hashedData, signature)
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    override fun isSavedKey(id: String): Boolean {
        if (WalletApi.isLock)
            throw WalletException(WalletErrorCode.ERR_CODE_WALLET_LOCKED_WALLET)

        return keyManager.isKeySaved(id)
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    override fun changePin(keyId: String, oldPin: String, newPin: String) {
        keyManager.changePin(keyId, oldPin.toByteArray(), newPin.toByteArray())
    }
}