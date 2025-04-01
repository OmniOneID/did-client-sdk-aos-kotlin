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

package org.omnione.did.sdk.utility.DataModels.ec

import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.math.ec.ECFieldElement
import org.spongycastle.math.ec.ECPoint

import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPrivateKeySpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.InvalidParameterSpecException
import java.security.spec.PKCS8EncodedKeySpec

object EcUtils {
    @JvmStatic
    fun getOrCreatePrivKeyBigInteger(value: ByteArray?): BigInteger = value?.let {
        when {
            (it[0].toInt() and 0x80) != 0 -> BigInteger(1, it)
            else -> BigInteger(it)
        }
    } ?: run {
        val curveName = "secp256r1"
        ECNamedCurveTable.getParameterSpec(curveName).let { ecParams ->
            val n = ecParams.n
            val nBitLength = n.bitLength()

            generateSequence {
                SecureRandom().let { random ->
                    ByteArray(nBitLength / 8).apply {
                        random.nextBytes(this)
                        this[0] = (this[0].toInt() and 0x7F).toByte()
                    }
                }
            }.first { BigInteger(1, it) in BigInteger.ONE..n.minus(BigInteger.ONE) }
                .let { BigInteger(1, it) }
        }
    }

    @JvmStatic
    fun getPubKey(bnum: BigInteger): ByteArray =
        ECNamedCurveTable.getParameterSpec("secp256r1").let { ecParams ->
            ecParams.g.multiply(bnum).let { Q ->
                compressPoint(Q)
            }
        }.let { compressedQ ->
            ByteArray(33).apply {
                val xBytes = compressedQ.x.encoded
                val prefixByte: Byte = if (compressedQ.y.toBigInteger().testBit(0)) 0x03 else 0x02

                this[0] = prefixByte
                System.arraycopy(xBytes, 0, this, 1, xBytes.size)
            }
        }

    @JvmStatic
    fun compressPoint(point: ECPoint): ECPoint = with(point) {
        val xBytes = x.encoded
        val prefixByte: Byte = if (y.toBigInteger().testBit(0)) 0x03 else 0x02

        curve.decodePoint(ByteArray(xBytes.size + 1).apply {
            this[0] = prefixByte
            System.arraycopy(xBytes, 0, this, 1, xBytes.size)
        })
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun getPublicKey(pubKeyBytes: ByteArray, ecType: String): ECPublicKey =
        ECNamedCurveTable.getParameterSpec(ecType).let { spec ->
            spec.curve.decodePoint(pubKeyBytes).let { ecPoint ->
                ECPublicKeySpec(ecPoint, spec)
            }
        }.let { publicKeySpec ->
            KeyFactory.getInstance("EC", "SC").let { keyFactory ->
                keyFactory.generatePublic(publicKeySpec) as ECPublicKey
            }
        }

    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidKeySpecException::class, InvalidParameterSpecException::class)
    fun getPrivateKey(privateKey: ByteArray, ecType: String): ECPrivateKey =
        AlgorithmParameters.getInstance("EC", "SC").apply {
            init(ECGenParameterSpec(ecType))
        }.let { parameters ->
            parameters.getParameterSpec(ECParameterSpec::class.java).let { ecParameterSpec ->
                ECPrivateKeySpec(BigInteger(privateKey), ecParameterSpec)
            }
        }.let { ecPrivateKeySpec ->
            KeyFactory.getInstance("ECDSA", "SC").let { keyFactory ->
                keyFactory.generatePrivate(ecPrivateKeySpec) as ECPrivateKey
            }
        }

    @JvmStatic
    fun getBytes(bigint: BigInteger): ByteArray = ByteArray(32).apply {
        bigint.toByteArray().let { bytes ->
            when {
                bytes.size <= this.size -> {
                    System.arraycopy(bytes, 0, this, this.size - bytes.size, bytes.size)
                }
                else -> {
                    require(bytes.size == 33 && bytes[0] == 0.toByte()) {
                        "Invalid BigInteger byte array size"
                    }
                    System.arraycopy(bytes, 1, this, 0, bytes.size - 1)
                }
            }
        }
    }

    @JvmStatic
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    fun convertCompressedPrivateKey(uncompressedPrivateKey: ByteArray): ByteArray? =
        PKCS8EncodedKeySpec(uncompressedPrivateKey).let { keySpec ->
            KeyFactory.getInstance("EC").let { keyFactory ->
                keyFactory.generatePrivate(keySpec)
            }
        }.let { privateKey ->
            (privateKey as? ECPrivateKey)?.s?.toByteArray()
        }
}