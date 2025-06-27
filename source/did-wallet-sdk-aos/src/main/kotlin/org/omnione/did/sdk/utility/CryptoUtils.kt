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

package org.omnione.did.sdk.utility

import org.omnione.did.sdk.utility.DataModels.CipherInfo
import org.omnione.did.sdk.utility.DataModels.EcKeyPair
import org.omnione.did.sdk.utility.DataModels.EcType
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DataModels.ec.EcUtils
import org.omnione.did.sdk.utility.Errors.UtilityErrorCode
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.spongycastle.crypto.CipherParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil
import java.security.*
import java.security.spec.InvalidKeySpecException
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    init {
        Security.removeProvider("SC")
        Security.addProvider(org.spongycastle.jce.provider.BouncyCastleProvider())
    }

    @JvmStatic
    @Throws(UtilityException::class)
    fun generateNonce(size: Int): ByteArray =
        when {
            size == 0 -> throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_INVALID_PARAMETER, "size")
            else -> ByteArray(size).apply {
                SecureRandom.getInstance("SHA1PRNG").nextBytes(this)
            }
        }

    @JvmStatic
    @Throws(UtilityException::class)
    fun generateECKeyPair(ecType: EcType.EC_TYPE): EcKeyPair =
        when (ecType.value) {
            EcType.EC_TYPE.SECP256_K1.value, EcType.EC_TYPE.SECP256_R1.value ->
                EcUtils.getOrCreatePrivKeyBigInteger(null).let { privateKeyBigInt ->
                    EcKeyPair(
                        privateKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, privateKeyBigInt.toByteArray()),
                        publicKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, EcUtils.getPubKey(privateKeyBigInt))
                    )
                }
            else -> throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_UNSUPPORTED_EC_TYPE)
        }

    @JvmStatic
    @Throws(UtilityException::class)
    fun generateSharedSecret(ecType: EcType.EC_TYPE, privateKey: ByteArray?, publicKey: ByteArray?): ByteArray {
        return privateKey?.let { priv ->
            publicKey?.let { pub ->
                if (ecType.value != EcType.EC_TYPE.SECP256_K1.value && ecType.value != EcType.EC_TYPE.SECP256_R1.value) {
                    throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_UNSUPPORTED_EC_TYPE)
                }

                runCatching {
                    EcUtils.getOrCreatePrivKeyBigInteger(priv).let { privKeyBigInt ->
                        val ecPrivateKey = EcUtils.getPrivateKey(privKeyBigInt.toByteArray(), ecType.value)

                        EcUtils.getPublicKey(pub, ecType.value)
                            ?: throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_FAIL_TO_CONVERT_PUBLIC_KEY_TO_EXTERNAL_REPRESENTATION)
                    }.let { ecPublicKey ->
                        val ciPubKey: CipherParameters = ECUtil.generatePublicKeyParameter(ecPublicKey)
                        val pub = ciPubKey as ECPublicKeyParameters

                        EcUtils.getOrCreatePrivKeyBigInteger(priv).let { privKeyBigInt ->
                            val ecPrivateKey = EcUtils.getPrivateKey(privKeyBigInt.toByteArray(), ecType.value)
                            val priv = ECUtil.generatePrivateKeyParameter(ecPrivateKey) as ECPrivateKeyParameters

                            pub.q.multiply(priv.d).normalize().let { P ->
                                if (P.isInfinity) throw IllegalStateException("invalid ECDH")
                                EcUtils.getBytes(P.affineXCoord.toBigInteger())
                            }
                        }
                    }
                }.getOrElse { e ->
                    when (e) {
                        is GeneralSecurityException -> throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_FAIL_TO_GENERATE_SHARED_SECRET_USING_ECDH, e)
                        else -> throw e
                    }
                }
            }
        } ?: throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_INVALID_PARAMETER, "privateKey/publicKey")
    }

    @JvmStatic
    @Throws(UtilityException::class)
    fun pbkdf2(password: ByteArray?, salt: ByteArray?, iterations: Int, derivedKeyLength: Int): ByteArray {
        return password?.let { pwd ->
            salt?.let { s ->
                runCatching {
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").let { factory ->
                        PBEKeySpec(
                            pwd.map { it.toChar() }.toCharArray(),
                            s,
                            iterations,
                            derivedKeyLength * 8
                        ).let { spec ->
                            factory.generateSecret(spec)
                        }.let { secretKey ->
                            SecretKeySpec(secretKey.encoded, "AES").encoded
                        }
                    }
                }.getOrElse { e ->
                    when (e) {
                        is NoSuchAlgorithmException, is InvalidKeySpecException ->
                            throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_FAIL_TO_DERIVE_KEY_USING_PBKDF2)
                        else -> throw e
                    }
                }
            }
        } ?: throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_INVALID_PARAMETER, "password/salt")
    }

    @JvmStatic
    @Throws(UtilityException::class)
    fun encrypt(plain: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray =
        runCatching {
            Cipher.getInstance("${info.type}/${info.mode}/${info.padding}").apply {
                SecretKeySpec(key, info.type.toString()).let { secretKey ->
                    iv?.let { nonNullIv ->
                        init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(nonNullIv))
                    } ?: init(Cipher.ENCRYPT_MODE, secretKey)
                }
            }.doFinal(plain)
        }.getOrElse { e ->
            when (e) {
                is GeneralSecurityException -> throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_FAIL_TO_ENCRYPT_USING_AES, e)
                else -> throw e
            }
        }

    @JvmStatic
    @Throws(UtilityException::class)
    fun decrypt(ciphertext: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray =
        runCatching {
            Cipher.getInstance("${info.type}/${info.mode}/${info.padding}").apply {
                SecretKeySpec(key, info.type.toString()).let { secretKey ->
                    iv?.let { nonNullIv ->
                        init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(nonNullIv))
                    } ?: init(Cipher.DECRYPT_MODE, secretKey)
                }
            }.doFinal(ciphertext)
        }.getOrElse { e ->
            when (e) {
                is GeneralSecurityException -> throw UtilityException(UtilityErrorCode.ERR_CODE_CRYPTO_UTILS_FAIL_TO_DECRYPT_USING_AES, e)
                else -> throw e
            }
        }
}