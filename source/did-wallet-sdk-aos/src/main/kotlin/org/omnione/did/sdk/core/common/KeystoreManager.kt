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

package org.omnione.did.sdk.core.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.keymanager.datamodel.KeyGenerationInfo
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.utility.DataModels.EcType
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DataModels.ec.EcUtils
import org.omnione.did.sdk.utility.MultibaseUtils
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.ASN1Integer
import org.spongycastle.asn1.ASN1Sequence
import org.spongycastle.asn1.DERSequence
import org.spongycastle.asn1.x9.X9IntegerConverter
import org.spongycastle.crypto.ec.CustomNamedCurves
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec
import org.spongycastle.math.ec.ECAlgorithms
import org.spongycastle.math.ec.ECPoint
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Security
import java.security.Signature
import java.security.SignatureException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.InvalidParameterSpecException
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.GCMParameterSpec
import kotlin.experimental.and

object KeystoreManager {
    private const val KEYSTORE_KEY_VALID_SECONDS = 5 * 600
    private const val SECURE_ENCRYPTOR_ALIAS_PREFIX = "opendid_wallet_encryption_"
    private const val SIGNATURE_MANAGER_ALIAS_PREFIX = "opendid_wallet_signature_"
    
    init {
        Security.removeProvider("SC")
        Security.addProvider(org.spongycastle.jce.provider.BouncyCastleProvider())
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun generateKey(context: Context, prefix: String, alias: String): KeyGenerationInfo {
        try {
            val keyGenerationInfo = KeyGenerationInfo().apply {
                algoType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
            }
            
            when {
                context == null -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "context")
                alias.isEmpty() -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "alias")
                isKeySaved(prefix, alias) -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_DUPLICATED_PARAMETER, "alias")
            }
            
            val ecPubKeyX = generateECKeyWithKeyStore(prefix + alias, context)
            keyGenerationInfo.publicKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, ecPubKeyX)
            
            return keyGenerationInfo
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY, e.message ?: "Unknown error")
        } catch (e: InvalidAlgorithmParameterException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY, e.message ?: "Unknown error")
        } catch (e: InvalidKeySpecException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY, e.message ?: "Unknown error")
        } catch (e: NoSuchProviderException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY, e.message ?: "Unknown error")
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun isKeySaved(prefix: String, alias: String?): Boolean {
        try {
            if (prefix.isEmpty()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "prefix")
            }
            
            val key = if (alias != null) prefix + alias else prefix
            
            val ks = KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            
            return ks.aliases().toList().any { it.contains(key) }
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class, KeyStoreException::class)
    fun getPublicKey(prefix: String, alias: String): ByteArray {
        try {
            val ks = KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            
            val entry = ks.getEntry(prefix + alias, null)
            val pubKey = (entry as? KeyStore.PrivateKeyEntry)?.certificate?.publicKey?.encoded
                ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION)
            
            val ecPubkeyX = ByteArray(33)
            val ecPubkeyY = ByteArray(32)
            
            System.arraycopy(pubKey, 27 + 32, ecPubkeyY, 0, 32)
            
            ecPubkeyX[0] = if ((ecPubkeyY[ecPubkeyY.size - 1] and 0x01) != 0.toByte()) {
                0x03
            } else {
                0x02
            }
            
            System.arraycopy(pubKey, 27, ecPubkeyX, 1, 32)
            
            Arrays.fill(ecPubkeyY, 0x00.toByte())
            Arrays.fill(pubKey, 0x00.toByte())
            
            return ecPubkeyX
        } catch (e: UnrecoverableEntryException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION)
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_PUBLIC_KEY_REPRESENTATION)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun deleteKey(prefix: String, alias: String?) {
        try {
            if (prefix.isEmpty()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "prefix")
            }
            
            val key = if (alias != null) prefix + alias else prefix
            val aliasList = getKeystoreAliasList()
            
            val ks = KeyStore.getInstance("AndroidKeyStore")
                ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY)
            
            ks.load(null)
            
            aliasList?.forEach { storeAlias ->
                if (storeAlias.contains(key)) {
                    ks.deleteEntry(storeAlias)
                }
            }
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY)
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_DELETE_SECURE_KEY)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun sign(alias: String, digest: ByteArray?): ByteArray {
        try {
            if (digest == null || digest.isEmpty()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "digest")
            }
            
            if (alias.isEmpty()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER, "alias")
            }
            
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            if (!isKeySaved(SIGNATURE_MANAGER_ALIAS_PREFIX, alias)) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
            }
            
            val privateKey = keyStore.getKey(SIGNATURE_MANAGER_ALIAS_PREFIX + alias, null) as java.security.PrivateKey
            
            val ecdsaSign = Signature.getInstance("NoneWithECDSA")
            ecdsaSign.initSign(privateKey)
            ecdsaSign.update(digest)
            
            val signedMsgFromKeyStore = ecdsaSign.sign()
            val pubKey = getPublicKey(SIGNATURE_MANAGER_ALIAS_PREFIX, alias)
            
            return compressedSignValue(signedMsgFromKeyStore, pubKey, digest)
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: InvalidKeyException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: SignatureException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        } catch (e: UnrecoverableEntryException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_SIGN, e.message ?: "Unknown error")
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun verify(publicKey: ByteArray, digest: ByteArray, signature: ByteArray): Boolean {
        try {
            if (signature.size != 65) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_INVALID_PARAMETER)
            }
            
            val unCompressedSignature = compressedToDer(signature)
            val pubKey = EcUtils.getPublicKey(publicKey, EcType.EC_TYPE.SECP256_R1.value)
            
            val ecdsaVerify = Signature.getInstance("NoneWithECDSA")
            ecdsaVerify.initVerify(pubKey)
            ecdsaVerify.update(digest)
            
            return ecdsaVerify.verify(unCompressedSignature)
        } catch (e: GeneralSecurityException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_VERIFY_SIGNATURE_FAILED)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_VERIFY_SIGNATURE_FAILED)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun encrypt(alias: String, plainData: ByteArray, context: Context): ByteArray {
        try {
            val secretKey = if (isExistAliasInKeyStore(alias)) {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                val keyEntry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
                    ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
                
                keyEntry.secretKey
            } else {
                val keyGenParameterSpec = if (isSupportStrongBox(context)) {
                    KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .setIsStrongBoxBacked(true)
                        .build()
                } else {
                    KeyGenParameterSpec.Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build()
                }
                
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val params = cipher.parameters.getParameterSpec(GCMParameterSpec::class.java)
            
            val byteStream = ByteArrayOutputStream()
            val dataStream = DataOutputStream(byteStream)
            
            dataStream.writeInt(params.tLen)
            val iv = params.iv
            dataStream.writeInt(iv.size)
            dataStream.write(iv)
            
            val plaintextStream = ByteArrayInputStream(plainData)
            val chunkSize = 4 * 1024
            val buffer = ByteArray(chunkSize)
            
            while (plaintextStream.available() > chunkSize) {
                val readBytes = plaintextStream.read(buffer)
                val ciphertextChunk = cipher.update(buffer, 0, readBytes)
                dataStream.write(ciphertextChunk)
            }
            
            val readBytes = plaintextStream.read(buffer)
            val ciphertextChunk = cipher.doFinal(buffer, 0, readBytes)
                ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
            
            dataStream.write(ciphertextChunk)
            return byteStream.toByteArray()
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: UnrecoverableEntryException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: NoSuchPaddingException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: InvalidAlgorithmParameterException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: InvalidKeyException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: IllegalBlockSizeException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: InvalidParameterSpecException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: BadPaddingException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        } catch (e: NoSuchProviderException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_ENCRYPTED_DATA)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun decrypt(alias: String, cipherData: ByteArray): ByteArray {
        try {
            if (!isExistAliasInKeyStore(alias)) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
            }
            
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val keyEntry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
                ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
            
            val byteStream = ByteArrayInputStream(cipherData)
            val dataStream = DataInputStream(byteStream)
            val tlen = dataStream.readInt()
            val iv = ByteArray(dataStream.readInt())
            dataStream.read(iv)
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
            
            cipher.init(Cipher.DECRYPT_MODE, keyEntry.secretKey, GCMParameterSpec(tlen, iv))
            val cipherStream = CipherInputStream(byteStream, cipher)
            
            val outputStream = ByteArrayOutputStream()
            byteCopy(cipherStream, outputStream)
            
            return outputStream.toByteArray()
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: UnrecoverableEntryException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: NoSuchPaddingException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: InvalidAlgorithmParameterException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        } catch (e: InvalidKeyException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_CREATE_DECRYPTED_DATA)
        }
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun getKeystoreAliasList(): List<String> {
        try {
            val ks = KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            return ks.aliases().toList()
        } catch (e: KeyStoreException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: CertificateException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)
        }
    }
    
    // Private methods
    
    @Throws(WalletCoreException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class, InvalidKeySpecException::class)
    private fun generateECKeyWithKeyStore(alias: String, context: Context): ByteArray {
        val keyGenParameterSpec = if (isSupportStrongBox(context)) {
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_NONE)
                //.setUserAuthenticationRequired(true)
                //.setUserAuthenticationValidityDurationSeconds(KEYSTORE_KEY_VALID_SECONDS)
                .setIsStrongBoxBacked(true)
                .build()
        } else {
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_NONE)
                //.setUserAuthenticationRequired(true)
                //.setUserAuthenticationValidityDurationSeconds(KEYSTORE_KEY_VALID_SECONDS)
                .build()
        }

        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        keyPairGenerator.initialize(keyGenParameterSpec)

        val keyPair = keyPairGenerator.generateKeyPair()
            ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_CREATE_SECURE_KEY)

        val keyFactory = KeyFactory.getInstance("EC")
        val ecPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(keyPair.public.encoded)) as ECPublicKey

        return copyPublicKey(ecPublicKey)

    }
    
    @Throws(WalletCoreException::class)
    private fun copyPublicKey(ecPublicKey: ECPublicKey): ByteArray {
        val ecPubkeyX = ByteArray(33)
        val ecPubkeyY = ByteArray(32)
        val pubKey = ecPublicKey.encoded
            ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_FAILED_TO_COPY_PUBLIC_KEY)
        
        System.arraycopy(pubKey, 27 + 32, ecPubkeyY, 0, 32)
        
        ecPubkeyX[0] = if ((ecPubkeyY[ecPubkeyY.size - 1] and 0x01) != 0.toByte()) {
            0x03
        } else {
            0x02
        }
        
        System.arraycopy(ecPublicKey.encoded, 27, ecPubkeyX, 1, 32)
        
        Arrays.fill(ecPubkeyY, 0x00.toByte())
        Arrays.fill(pubKey, 0x00.toByte())
        
        return ecPubkeyX
    }
    
    private fun isSupportStrongBox(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && 
               context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }
    
    @Throws(WalletCoreException::class, KeyStoreException::class, CertificateException::class, IOException::class, NoSuchAlgorithmException::class)
    private fun isExistAliasInKeyStore(strAlias: String): Boolean {
        val ks = KeyStore.getInstance("AndroidKeyStore")
        ks.load(null)

        val aliases = ks.aliases()
            ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_KEYSTORE_MANAGER_CANNOT_FIND_SECURE_KEY_BY_CONDITION)

        return aliases.toList().any { it == strAlias }
    }
    
    private fun isBiometric(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    @Throws(IOException::class)
    private fun byteCopy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }
    
    @Throws(IOException::class)
    private fun compressedSignValue(derSignature: ByteArray, compressedPubKey: ByteArray, hashedSource: ByteArray): ByteArray {
        val asn1Sequence = parseASN1Sequence(derSignature)
        val (integerR, integerS) = extractRS(asn1Sequence)
        val adjustedS = adjustS(integerS, EcType.EC_TYPE.SECP256_R1.value)
        
        val r = padTo32Bytes(integerR)
        val s = padTo32Bytes(adjustedS)
        val recoveryId = getRecoveryId(r, s, hashedSource, compressedPubKey)
        
        return ByteBuffer.allocate(65).apply {
            put((recoveryId + 27 + 4).toByte())
            put(r)
            put(s)
        }.array()
    }
    
    private fun getRecoveryId(sigR: ByteArray, sigS: ByteArray, hashedMessage: ByteArray, publicKey: ByteArray): Byte {
        val spec = ECNamedCurveTable.getParameterSpec(EcType.EC_TYPE.SECP256_R1.value)
        val pointN = spec.n
        
        for (recoveryId in 0..1) {
            val pointX = BigInteger(1, sigR)
            val pointR = decodePoint(spec, pointX, recoveryId)
            
            if (!pointR.multiply(pointN).isInfinity) continue
            
            val pointQ = recoverPublicKey(spec, pointR, BigInteger(1, sigS), BigInteger(1, hashedMessage))
            if (publicKey.contentEquals(pointQ.getEncoded(true))) {
                return recoveryId.toByte()
            }
        }
        return 0
    }
    
    @Throws(IOException::class)
    private fun parseASN1Sequence(signData: ByteArray): ASN1Sequence {
        return ASN1InputStream(ByteArrayInputStream(signData)).use { it.readObject() as ASN1Sequence }
    }
    
    private fun extractRS(asn1Sequence: ASN1Sequence): Pair<BigInteger, BigInteger> {
        val asn1Encodables = asn1Sequence.toArray()
        val r = (asn1Encodables[0] as ASN1Integer).value
        val s = (asn1Encodables[1] as ASN1Integer).value
        return Pair(r, s)
    }
    
    private fun adjustS(s: BigInteger, curveName: String): BigInteger {
        val curveN = CustomNamedCurves.getByName(curveName).n
        val halfCurveOrder = curveN.shiftRight(1)
        return if (s > halfCurveOrder) curveN.subtract(s) else s
    }
    
    private fun padTo32Bytes(value: BigInteger): ByteArray {
        val byteArray = value.toByteArray()
        return ByteArray(32).apply {
            if (byteArray.size > 32) {
                System.arraycopy(byteArray, byteArray.size - 32, this, 0, 32)
            } else {
                System.arraycopy(byteArray, 0, this, 32 - byteArray.size, byteArray.size)
            }
        }
    }
    
    private fun decodePoint(spec: ECNamedCurveParameterSpec, pointX: BigInteger, recoveryId: Int): ECPoint {
        val x9 = X9IntegerConverter()
        val compEnc = x9.integerToBytes(pointX, 1 + x9.getByteLength(spec.curve))
        compEnc[0] = if ((recoveryId and 1) == 1) 0x03.toByte() else 0x02.toByte()
        return spec.curve.decodePoint(compEnc)
    }
    
    private fun recoverPublicKey(spec: ECNamedCurveParameterSpec, pointR: ECPoint, sigS: BigInteger, message: BigInteger): ECPoint {
        val pointN = spec.n
        val pointEInv = message.negate().mod(pointN)
        val pointRInv = pointR.xCoord.toBigInteger().modInverse(pointN)
        val srInv = pointRInv.multiply(sigS).mod(pointN)
        val pointEInvRInv = pointRInv.multiply(pointEInv).mod(pointN)
        return ECAlgorithms.sumOfTwoMultiplies(spec.g, pointEInvRInv, pointR, srInv)
    }
    
    @Throws(IOException::class)
    private fun compressedToDer(compactSignature: ByteArray): ByteArray {
        val rBytes = compactSignature.copyOfRange(1, 33)
        val sBytes = compactSignature.copyOfRange(33, 65)
        val r = BigInteger(1, rBytes)
        val s = BigInteger(1, sBytes)
        
        return DERSequence(arrayOf(ASN1Integer(r), ASN1Integer(s))).encoded
    }
}
