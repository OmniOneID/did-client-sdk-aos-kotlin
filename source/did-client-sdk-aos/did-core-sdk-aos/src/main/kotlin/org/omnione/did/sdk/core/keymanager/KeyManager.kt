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

package org.omnione.did.sdk.core.keymanager

import android.content.Context
import org.omnione.did.sdk.core.common.KeystoreManager
import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.keymanager.datamodel.*
import org.omnione.did.sdk.core.keymanager.supportalgorithm.Secp256K1Manager
import org.omnione.did.sdk.core.keymanager.supportalgorithm.Secp256R1Manager
import org.omnione.did.sdk.core.keymanager.supportalgorithm.SignableInterface
import org.omnione.did.sdk.core.storagemanager.StorageManager
import org.omnione.did.sdk.core.storagemanager.datamodel.FileExtension
import org.omnione.did.sdk.core.storagemanager.datamodel.UsableInnerWalletItem
import org.omnione.did.sdk.datamodel.common.BaseObject
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.datamodel.common.enums.AuthType
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.DataModels.CipherInfo
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.utility.MultibaseUtils
import java.util.*

class KeyManager<E : BaseObject> {
    private lateinit var ctx: Context
    private lateinit var storageManager: StorageManager<KeyInfo, DetailKeyInfo>

    companion object {
        private const val SIGNATURE_MANAGER_ALIAS_PREFIX = "opendid_wallet_signature_"
    }

    constructor()

    @Throws(WalletCoreException::class)
    constructor(fileName: String, ctx: Context) {
        this.ctx = ctx
        storageManager = StorageManager(
            fileName,
            FileExtension.FILE_EXTENSION.KEY,
            true,
            ctx,
            DetailKeyInfo::class.java,
            KeyInfo::class.java
        )
    }

    /**
     * isKeySaved: Checks if a key with the given ID is saved.
     *
     * This method verifies if a key with the specified ID is stored. It first checks if the ID is valid,
     * then confirms if any key is saved, and finally checks if the key with the provided ID exists in the storage.
     *
     * @param id The identifier of the key to be checked.
     * @return Returns true if the key with the given ID is saved, false otherwise.
     * @throws WalletCoreException Throws an exception if the ID parameter is null or if an error occurs during the check.
     * @throws UtilityException Throws if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun isKeySaved(id: String): Boolean {
        if (id.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "id"
            )
        }
        if (!isAnyKeySaved()) {
            return false
        }
        return storageManager.getMetas(listOf(id)).isNotEmpty()
    }

    /**
     * Generate a key based on the provided key generation request.
     *
     * This method generates a key based on the type of KeyGenRequest provided.
     * It first validates the ID from the request and checks if a key with the same ID already exists.
     * Depending on the type of KeyGenRequest (WalletKeyGenRequest or SecureKeyGenRequest), it delegates
     * the key generation process to the appropriate method.
     *
     * @param keyGenRequest The request object containing key generation details.
     * @throws WalletCoreException If the keyGenRequest ID is null, if a key with the same ID already exists,
     *                             or if the keyGenRequest does not conform to a recognized key generation request type.
     * @throws UtilityException If utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun generateKey(keyGenRequest: KeyGenRequest) {
        if (keyGenRequest.id.isNullOrEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "keyGenRequest.id"
            )
        }

        if (storageManager.isSaved()) {
            val keyInfos = storageManager.getAllMetas()
            for (keyInfo in keyInfos) {
                if (keyGenRequest.id == keyInfo.id) {
                    throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_EXIST_KEY_ID)
                }
            }
        }

        when (keyGenRequest) {
            is WalletKeyGenRequest -> generateKeyForWallet(keyGenRequest)
            is SecureKeyGenRequest -> generateKeyForSecure(keyGenRequest)
            else -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_NOT_CONFORM_TO_KEY_GEN_REQUEST)
        }
    }

    /**
     * Generates a key for the wallet.
     *
     * This method generates a key for the wallet using the provided WalletKeyGenRequest object.
     *
     * @param keyGenRequest the request object containing parameters for key generation
     * @throws WalletCoreException if an error occurs during key generation or encryption
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    private fun generateKeyForWallet(keyGenRequest: WalletKeyGenRequest) {
        val genKeyInfo = when (keyGenRequest.algorithmType.value) {
            AlgorithmType.ALGORITHM_TYPE.SECP256R1.value -> {
                getKeyAlgorithm<Secp256R1Manager>(keyGenRequest.algorithmType).generateKey()
            }

            else -> KeyGenerationInfo()
        }
        if (genKeyInfo == null) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "genKeyInfo"
            )
        }
        val priKey = MultibaseUtils.decode(genKeyInfo.privateKey)

        if (keyGenRequest.methodType.walletMethodType == KeyGenWalletMethodType.WALLET_METHOD_TYPE.PIN) {
            if (keyGenRequest.methodType.pin.isEmpty()) {
                throw WalletCoreException(
                    WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                    "pin"
                )
            }

            val salt = CryptoUtils.generateNonce(32)
            val iteration = 2048
            val symmetricKey = CryptoUtils.pbkdf2(
                MultibaseUtils.decode(keyGenRequest.methodType.pin),
                salt,
                iteration,
                48
            )
            val key = symmetricKey.copyOfRange(0, 32)
            val iv = symmetricKey.copyOfRange(32, symmetricKey.size)
            val ePriKeyByte = CryptoUtils.encrypt(
                priKey,
                CipherInfo(
                    CipherInfo.ENCRYPTION_TYPE.AES,
                    CipherInfo.ENCRYPTION_MODE.CBC,
                    CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                    CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                ),
                key,
                iv
            )

            val encodedPubKey = genKeyInfo.publicKey
            val encodedPriKey =
                MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, ePriKeyByte)

            val keyInfo = KeyInfo(
                keyGenRequest.id,
                AuthType.AUTH_TYPE.PIN,
                keyGenRequest.algorithmType,
                encodedPubKey,
                KeyAccessMethod.KEY_ACCESS_METHOD.WALLET_PIN
            )

            val detailKeyInfo = DetailKeyInfo(
                keyGenRequest.id,
                encodedPriKey,
                MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, salt)
            )

            Arrays.fill(symmetricKey, 0x00.toByte())
            Arrays.fill(priKey, 0x00.toByte())
            Arrays.fill(ePriKeyByte, 0x00.toByte())
            genKeyInfo.privateKey = ""

            storageManager.addItem(getUsableInnerWalletItem(keyInfo, detailKeyInfo), false)
        } else {
            val encodedPubKey = genKeyInfo.publicKey
            val encodedPriKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, priKey)

            val keyInfo = KeyInfo(
                keyGenRequest.id,
                AuthType.AUTH_TYPE.FREE,
                keyGenRequest.algorithmType,
                encodedPubKey,
                KeyAccessMethod.KEY_ACCESS_METHOD.WALLET_NONE
            )

            val detailKeyInfo = DetailKeyInfo(
                keyGenRequest.id,
                encodedPriKey
            )

            Arrays.fill(priKey, 0x00.toByte())
            genKeyInfo.privateKey = ""

            storageManager.addItem(getUsableInnerWalletItem(keyInfo, detailKeyInfo), true)
        }
    }

    /**
     * Generates a secure key for the keystore.
     *
     * This method generates a key for the keystore using the provided SecureKeyGenRequest object. It checks if a key
     * with the given ID already exists and throws an exception if it does. It supports different access methods, including
     * biometric access via the keystore.
     *
     * @param keyGenRequest the request object containing parameters for secure key generation
     * @throws WalletCoreException if an error occurs during key generation or if a key with the given ID already exists
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    private fun generateKeyForSecure(keyGenRequest: SecureKeyGenRequest) {
        if (KeystoreManager.isKeySaved(SIGNATURE_MANAGER_ALIAS_PREFIX, keyGenRequest.id)) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "alias"
            )
        }

        val encodedPubKey = KeystoreManager.generateKey(
            ctx,
            SIGNATURE_MANAGER_ALIAS_PREFIX,
            keyGenRequest.id
        ).publicKey

        val keyInfo = KeyInfo(
            keyGenRequest.id,
            AuthType.AUTH_TYPE.BIO,
            keyGenRequest.algorithmType,
            encodedPubKey,
            KeyAccessMethod.KEY_ACCESS_METHOD.KEYSTORE_BIOMETRY
        )

        val detailKeyInfo = DetailKeyInfo(keyGenRequest.id)

        storageManager.addItem(getUsableInnerWalletItem(keyInfo, detailKeyInfo), false)
    }

    /**
     * Changes the PIN for a given ID.
     *
     * This method verifies the user's current PIN and then replaces it with a new one.
     * It ensures that the new PIN is different from the old PIN and that the keys are decrypted correctly.
     *
     * @param id The identifier of the key to be changed.
     * @param oldPin The current PIN.
     * @param newPin The new PIN.
     * @throws WalletCoreException Throws an exception if parameter validation fails or if an error occurs during encryption/decryption.
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun changePin(id: String, oldPin: ByteArray, newPin: ByteArray) {
        if (id.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "id"
            )
        }

        val identifiers = listOf(id)
        val walletItems = storageManager.getItems(identifiers)

        if (oldPin.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "oldPin"
            )
        }

        if (newPin.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "newPin"
            )
        }

        if (oldPin.contentEquals(newPin)) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_NEW_PIN_EQUALS_OLD_PIN)
        }

        val oldKeyInfo = walletItems[0].meta
        val oldDetailKeyInfo = walletItems[0].item

        if (oldKeyInfo.authType != AuthType.AUTH_TYPE.PIN) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_NOT_PIN_AUTH_TYPE)
        }

        val ePrivateKey = MultibaseUtils.decode(oldDetailKeyInfo.privateKey)
            ?: throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                "Data(R)"
            )

        val salt = MultibaseUtils.decode(oldDetailKeyInfo.salt)
            ?: throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                "Data(A)"
            )

        val symmetricKey = CryptoUtils.pbkdf2(oldPin, salt, 2048, 48)
        val key = symmetricKey.copyOfRange(0, 32)
        val iv = symmetricKey.copyOfRange(32, symmetricKey.size)
        val priKey = CryptoUtils.decrypt(
            ePrivateKey,
            CipherInfo(
                CipherInfo.ENCRYPTION_TYPE.AES,
                CipherInfo.ENCRYPTION_MODE.CBC,
                CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
            ),
            key,
            iv
        )

        val pubKey = MultibaseUtils.decode(oldKeyInfo.publicKey)
            ?: throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                "Data(U)"
            )

        if (oldKeyInfo.algorithm.value == AlgorithmType.ALGORITHM_TYPE.SECP256R1.value) {
            val keyAlgorithm = getKeyAlgorithm<Secp256R1Manager>(oldKeyInfo.algorithm)
            keyAlgorithm.checkKeyPairMatch(priKey, pubKey)
        }

        val newSalt = CryptoUtils.generateNonce(32)
        val newIteration = 2048
        val newSymmetricKey = CryptoUtils.pbkdf2(newPin, newSalt, newIteration, 48)
        val newKey = newSymmetricKey.copyOfRange(0, 32)
        val newIv = newSymmetricKey.copyOfRange(32, newSymmetricKey.size)
        val newEncryptedPrivateKey = CryptoUtils.encrypt(
            priKey,
            CipherInfo(
                CipherInfo.ENCRYPTION_TYPE.AES,
                CipherInfo.ENCRYPTION_MODE.CBC,
                CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
            ),
            newKey,
            newIv
        )

        val strEncodedPriKey =
            MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, newEncryptedPrivateKey)
        val newKeyPairInfo = DetailKeyInfo(
            id,
            strEncodedPriKey,
            MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, newSalt)
        )

        val newWalletItem = UsableInnerWalletItem<KeyInfo, DetailKeyInfo>().apply {
            item = newKeyPairInfo
            meta = oldKeyInfo
        }

        storageManager.updateItem(newWalletItem)

        // Cleanup sensitive data
        Arrays.fill(symmetricKey, 0x00.toByte())
        Arrays.fill(priKey, 0x00.toByte())
        Arrays.fill(newSymmetricKey, 0x00.toByte())
        Arrays.fill(newEncryptedPrivateKey, 0x00.toByte())
    }

    /**
     * Returns key information for the given list of IDs.
     *
     * This method retrieves key information based on the provided list of IDs.
     * If the list of IDs is empty or contains duplicates, it throws an exception.
     *
     * @param ids List of IDs for which to retrieve key information.
     * @return A list of KeyInfo objects corresponding to the provided IDs.
     * @throws WalletCoreException If the list of IDs is empty or contains duplicates.
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun getKeyInfos(ids: List<String>): List<KeyInfo> {
        if (ids.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "ids"
            )
        }

        if (ids.size != ids.toSet().size) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_DUPLICATED_PARAMETER,
                "ids"
            )
        }

        return storageManager.getMetas(ids)
    }

    /**
     * Retrieves key information based on the specified authentication type.
     *
     * This method fetches all key information and filters it based on the provided
     * authentication type. If the specified authentication type is 0, it returns all keys.
     * If no keys match the specified authentication type, or if the key type is invalid,
     * it throws an exception.
     *
     * @param keyType The type of authentication used to filter keys.
     * @return A list of KeyInfo objects that match the specified authentication type.
     * @throws WalletCoreException If no keys match the specified authentication type or if the key type is invalid.
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun getKeyInfos(keyType: VerifyAuthType.VERIFY_AUTH_TYPE): List<KeyInfo> {
        val metas = storageManager.getAllMetas()

        if (keyType.intValue == 0) {
            return metas
        }

        var checkKeyType = keyType.intValue
        val keyInfos = mutableListOf<KeyInfo>()

        for (keyInfo in metas) {
            if ((keyInfo.authType.intValue and checkKeyType) != 0) {
                checkKeyType -= keyInfo.authType.intValue
                keyInfos.add(keyInfo)
            }
        }

        if (checkKeyType == keyType.intValue) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FOUND_NO_KEY_BY_KEY_TYPE)
        }

        if (checkKeyType > VerifyAuthType.VERIFY_AUTH_TYPE.PIN_AND_BIO.intValue) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INSUFFICIENT_RESULT_BY_KEY_TYPE)
        }

        return keyInfos
    }

    /**
     * Deletes the keys corresponding to the given list of IDs.
     *
     * This method deletes the keys corresponding to the provided list of IDs.
     * It throws an exception if the list is empty or contains duplicate IDs.
     * Additionally, it individually deletes biometric keys stored in the keystore.
     *
     * @param ids List of key IDs to be deleted.
     * @throws WalletCoreException If the list of IDs is empty, contains duplicate IDs, or an error occurs during key deletion.
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun deleteKeys(ids: List<String>) {
        if (ids.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "ids"
            )
        }

        if (ids.size != ids.toSet().size) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_DUPLICATED_PARAMETER,
                "ids"
            )
        }

        val metas = storageManager.getMetas(ids)

        for (meta in metas) {
            if (meta.accessMethod.intValue == KeyAccessMethod.KEY_ACCESS_METHOD.KEYSTORE_BIOMETRY.intValue) {
                KeystoreManager.deleteKey(SIGNATURE_MANAGER_ALIAS_PREFIX, meta.id)
            }
        }

        storageManager.removeItems(ids)
    }

    /**
     * Deletes all keys stored in the system.
     *
     * This method removes all key items from the storage manager and, if any keys are
     * saved in the keystore under the specified alias prefix, deletes them as well.
     *
     * @throws WalletCoreException If an error occurs during the deletion of all keys from storage or keystore.
     */
    @Throws(WalletCoreException::class)
    fun deleteAllKeys() {
        storageManager.removeAllItems()

        if (KeystoreManager.isKeySaved(SIGNATURE_MANAGER_ALIAS_PREFIX, null)) {
            KeystoreManager.deleteKey(SIGNATURE_MANAGER_ALIAS_PREFIX, null)
        }
    }

    /**
     * Signs the given digest using the specified key identified by its ID and PIN.
     *
     * This method retrieves the key associated with the given ID and signs the provided digest.
     * The key may be accessed either directly, via PIN, or through a keystore. Depending on the
     * access method, it may use a different signing approach.
     *
     * @param id The identifier for the key to use for signing.
     * @param pin The PIN for unlocking the key if required (can be null if not using PIN access).
     * @param digest The digest to sign with the key.
     * @return The generated signature.
     * @throws WalletCoreException If any error occurs during the process, such as invalid parameters,
     *                            failure to decode data, or unsupported algorithms.
     * @throws UtilityException if utility operations fail
     */
    @Throws(WalletCoreException::class, UtilityException::class)
    fun sign(id: String, pin: ByteArray?, digest: ByteArray?): ByteArray {
        if (id.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "id"
            )
        }

        if (digest == null) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "digest"
            )
        }

        val identifiers = listOf(id)
        val walletItems = storageManager.getItems(identifiers)
        val signKeyInfo = walletItems[0].meta
        val signDetailKeyInfo = walletItems[0].item

        val publicKey = MultibaseUtils.decode(signKeyInfo.publicKey)

        return when (signKeyInfo.accessMethod) {
            KeyAccessMethod.KEY_ACCESS_METHOD.WALLET_NONE -> {
                val decPrivateKey = MultibaseUtils.decode(signDetailKeyInfo.privateKey)
                    ?: throw WalletCoreException(
                        WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                        "Data(R)"
                    )

                val signature = if (signKeyInfo.algorithm.value == "Secp256r1") {
                    val keyGen = Secp256R1Manager()
                    keyGen.sign(decPrivateKey, digest)
                } else {
                    throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM)
                }

                Arrays.fill(decPrivateKey, 0x00.toByte())
                signature
            }

            KeyAccessMethod.KEY_ACCESS_METHOD.WALLET_PIN -> {
                if (pin == null) {
                    throw WalletCoreException(
                        WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                        "pin"
                    )
                }

                val multibaseDecoded = MultibaseUtils.decode(signDetailKeyInfo.privateKey)
                    ?: throw WalletCoreException(
                        WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                        "Data(R)"
                    )

                val salt = MultibaseUtils.decode(signDetailKeyInfo.salt)
                    ?: throw WalletCoreException(
                        WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_FAILED_TO_DECODE,
                        "Data(A)"
                    )

                val iteration = 2048
                val symmetricKey = CryptoUtils.pbkdf2(pin, salt, iteration, 48)
                val key = symmetricKey.copyOfRange(0, 32)
                val iv = symmetricKey.copyOfRange(32, symmetricKey.size)
                val pinDecoded = CryptoUtils.decrypt(
                    multibaseDecoded,
                    CipherInfo(
                        CipherInfo.ENCRYPTION_TYPE.AES,
                        CipherInfo.ENCRYPTION_MODE.CBC,
                        CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                        CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                    ),
                    key,
                    iv
                )

                val keyAlgorithm = Secp256R1Manager()
                keyAlgorithm.checkKeyPairMatch(pinDecoded, publicKey)

                val signature = if (signKeyInfo.algorithm.value == "Secp256r1") {
                    keyAlgorithm.sign(pinDecoded, digest)
                } else {
                    throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM)
                }

                // Cleanup sensitive data
                Arrays.fill(pin, 0x00.toByte())
                Arrays.fill(multibaseDecoded, 0x00.toByte())
                Arrays.fill(salt, 0x00.toByte())
                Arrays.fill(symmetricKey, 0x00.toByte())
                Arrays.fill(pinDecoded, 0x00.toByte())

                signature
            }

            KeyAccessMethod.KEY_ACCESS_METHOD.KEYSTORE_NONE,
            KeyAccessMethod.KEY_ACCESS_METHOD.KEYSTORE_BIOMETRY -> KeystoreManager.sign(id, digest)

            else -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM)
        }
    }


    /**
     * Verifies the signature for the given digest using the specified public key and algorithm.
     *
     * This method checks the validity of a signature by using the given public key, digest, and algorithm type.
     * It validates the input parameters, selects the appropriate verification method based on the algorithm type,
     * and returns whether the signature is valid or not.
     *
     * @param algorithmType The algorithm type used for verification (e.g., SECP256R1).
     * @param publicKey The public key used to verify the signature. Expected length is 33 bytes.
     * @param digest The digest of the data that was signed. Should have a length of at least 1 byte.
     * @param signature The signature to be verified. Expected length is 65 bytes.
     * @return True if the signature is valid, false otherwise.
     * @throws WalletCoreException Throws an exception if any parameter is invalid or if the algorithm type is unsupported.
     */
    @Throws(WalletCoreException::class)
    fun verify(
        algorithmType: AlgorithmType.ALGORITHM_TYPE,
        publicKey: ByteArray,
        digest: ByteArray,
        signature: ByteArray
    ): Boolean {
        if (publicKey.size != 33) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "publicKey"
            )
        }
        if (digest.isEmpty()) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "digest"
            )
        }
        if (signature.size != 65) {
            throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_INVALID_PARAMETER,
                "signature"
            )
        }

        return when (algorithmType) {
            AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> {
                val secp256R1Manager = Secp256R1Manager()
                secp256R1Manager.verify(publicKey, digest, signature)
            }

            else -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM)
        }
    }

    /**
     * Checks if there are any keys saved in the storage.
     *
     * This method uses the `storageManager` to determine if there are any keys currently stored in the storage.
     * It returns true if at least one key is saved in the storage; otherwise, it returns false.
     *
     * @return true if there are any keys saved in the storage; false otherwise.
     */
    fun isAnyKeySaved(): Boolean {
        return storageManager.isSaved()
    }

    /**
     * Creates a [UsableInnerWalletItem] instance with the provided [KeyInfo] and [DetailKeyInfo].
     *
     * @param keyInfo The metadata for the key.
     * @param detailKeyInfo The detailed information about the key.
     * @return A [UsableInnerWalletItem] containing the provided key information.
     */
    private fun getUsableInnerWalletItem(
        keyInfo: KeyInfo,
        detailKeyInfo: DetailKeyInfo
    ): UsableInnerWalletItem<KeyInfo, DetailKeyInfo> {
        return UsableInnerWalletItem<KeyInfo, DetailKeyInfo>().apply {
            item = detailKeyInfo
            meta = keyInfo
        }
    }

    /**
     * Retrieves the appropriate key algorithm manager based on the specified algorithm type.
     *
     * @param algorithmType The type of algorithm to retrieve the manager for.
     * @return An instance of the key algorithm manager.
     * @throws WalletCoreException If the algorithm type is unsupported.
     */
    @Throws(WalletCoreException::class)
    private fun <T : SignableInterface> getKeyAlgorithm(algorithmType: AlgorithmType.ALGORITHM_TYPE): T {
        return when (algorithmType) {
            AlgorithmType.ALGORITHM_TYPE.RSA -> throw UnsupportedOperationException("RSA is not implemented yet")
            AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> Secp256K1Manager() as T
            AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> Secp256R1Manager() as T
            else -> throw WalletCoreException(
                WalletCoreErrorCode.ERR_CODE_KEY_MANAGER_UNSUPPORTED_ALGORITHM,
                algorithmType.value
            )
        }
    }
}