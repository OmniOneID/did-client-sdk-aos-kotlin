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

package org.omnione.did.sdk.core.keymanager.supportalgorithm;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;

import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.keymanager.datamodel.KeyGenerationInfo
import org.omnione.did.sdk.core.keymanager.supportalgorithm.SignableInterface

import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType
import org.omnione.did.sdk.utility.DataModels.EcType
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.DataModels.ec.EcUtils
import org.omnione.did.sdk.utility.MultibaseUtils
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.jce.provider.BouncyCastleProvider

import java.io.ByteArrayInputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.*
import java.security.spec.*
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException

class Secp256R1Manager : SignableInterface {

    companion object {
        init {
            Security.removeProvider("SC")
            Security.addProvider(BouncyCastleProvider())
        }
    }

    private val SIGNATURE_MANAGER_ALIAS_PREFIX = "opendid_wallet_signature_"

    override fun generateKey(): KeyGenerationInfo {
        val keyGenerationInfo = KeyGenerationInfo()
        keyGenerationInfo.algoType = AlgorithmType.ALGORITHM_TYPE.SECP256R1
        val bNum = getOrCreatePrivKeyBigInteger(null)
        keyGenerationInfo.privateKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, bNum.toByteArray())
        keyGenerationInfo.publicKey = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, getPubKey(bNum))

        return keyGenerationInfo
    }

    @Throws(WalletCoreException::class)
    override fun sign(privateKey: ByteArray, digest: ByteArray): ByteArray {
        return try {
            val ecSpec = ECGenParameterSpec("secp256r1")
            val restoredPrivateKey = getECPrivateKeyFromBytes(privateKey, ecSpec)
            val bNum = getOrCreatePrivKeyBigInteger(privateKey)
            val pubKey = getPubKey(bNum)
            val ecdsaSign = Signature.getInstance("NoneWithECDSA")
            ecdsaSign.initSign(restoredPrivateKey)
            ecdsaSign.update(digest)
            val signature = ecdsaSign.sign()
            compressedSignValue(signature, pubKey, digest)
        } catch (e: InvalidKeyException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        } catch (e: SignatureException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        } catch (e: NoSuchAlgorithmException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        } catch (e: InvalidAlgorithmParameterException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        } catch (e: InvalidKeySpecException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        }
    }

    @Throws(NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, InvalidKeySpecException::class)
    private fun getECPrivateKeyFromBytes(privateKeyBytes: ByteArray, ecSpec: ECGenParameterSpec): PrivateKey {
        val s = BigInteger(1, privateKeyBytes)
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ecSpec)
        val kp = kpg.generateKeyPair()
        val params = (kp.public as java.security.interfaces.ECPublicKey).params

        val privateKeySpec = ECPrivateKeySpec(s, params)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePrivate(privateKeySpec)
    }

    @Throws(WalletCoreException::class)
    override fun verify(publicKey: ByteArray, digest: ByteArray, signature: ByteArray): Boolean {
        return try {
            if (signature.size != 65) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_INVALID_PARAMETER, "signature")
            }
            val unCompressedSignature = compressedToDer(signature)
            val pubKey = EcUtils.getPublicKey(publicKey, EcType.EC_TYPE.SECP256_R1.value)
            val ecdsaVerify = Signature.getInstance("NoneWithECDSA")
            ecdsaVerify.initVerify(pubKey)
            ecdsaVerify.update(digest)
            ecdsaVerify.verify(unCompressedSignature)
        } catch (e: GeneralSecurityException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_VERIFY_SIGNATURE_FAILED)
        } catch (e: IOException) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_VERIFY_SIGNATURE_FAILED)
        }
    }

    @Throws(WalletCoreException::class)
    fun checkKeyPairMatch(privateKey: ByteArray, publicKey: ByteArray) {
        val bNum = getOrCreatePrivKeyBigInteger(privateKey)
        val pubKey = getPubKey(bNum)
        if (!publicKey.contentEquals(pubKey)) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_NOT_DERIVED_KEY_FROM_PRIVATE_KEY)
        }
    }

    @Throws(IOException::class, WalletCoreException::class)
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

    @Throws(IOException::class)
    private fun compressedToDer(compactSignature: ByteArray): ByteArray {
        val rBytes = compactSignature.copyOfRange(1, 33)
        val r = BigInteger(1, rBytes)

        val sBytes = compactSignature.copyOfRange(33, 65)
        val s = BigInteger(1, sBytes)

        val v = ASN1EncodableVector().apply {
            add(ASN1Integer(r))
            add(ASN1Integer(s))
        }

        return DERSequence(v).encoded
    }

    private fun getOrCreatePrivKeyBigInteger(value: ByteArray?): BigInteger {
        if (value != null) {
            return if ((value[0].toInt() and 0x80) != 0) {
                BigInteger(1, value)
            } else {
                BigInteger(value)
            }
        }

        val curveName = "secp256r1"
        val ecParams = SECNamedCurves.getByName(curveName)
        val n = ecParams.n

        val nBitLength = n.bitLength()
        val mSecRandom = SecureRandom()

        while (true) {
            val bytes = ByteArray(nBitLength / 8)
            mSecRandom.nextBytes(bytes)
            bytes[0] = (bytes[0].toInt() and 0x7F).toByte()
            val privBigInteger = BigInteger(1, bytes)
            if (privBigInteger != BigInteger.ZERO && privBigInteger < n) {
                return privBigInteger
            }
        }
    }


    private fun getPubKey(bnum: BigInteger): ByteArray {
        val curveName = "secp256r1"
        val ecParams = SECNamedCurves.getByName(curveName)

        val G = ecParams.g
        val Q = G.multiply(bnum)
        val compressedQ = compressPoint(Q)

        val publicKeyBytes = ByteArray(33)
        val xBytes = compressedQ.xCoord.encoded
        val prefixByte = if (compressedQ.yCoord.toBigInteger().testBit(0)) 0x03.toByte() else 0x02.toByte()

        publicKeyBytes[0] = prefixByte
        System.arraycopy(xBytes, 0, publicKeyBytes, 1, xBytes.size)

        return publicKeyBytes
    }

    private fun compressPoint(point: ECPoint): ECPoint {
        val curve = point.curve
        val x = point.x
        val y = point.y

        val xBytes = x.encoded
        val prefixByte = if (y.toBigInteger().testBit(0)) 0x03.toByte() else 0x02.toByte()

        val compressedBytes = ByteArray(xBytes.size + 1)
        compressedBytes[0] = prefixByte
        System.arraycopy(xBytes, 0, compressedBytes, 1, xBytes.size)

        return curve.decodePoint(compressedBytes)
    }

    @Throws(WalletCoreException::class)
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
    private fun parseASN1Sequence(signData: ByteArray): ASN1Sequence {
        ByteArrayInputStream(signData).use { inStream ->
            ASN1InputStream(inStream).use { asnInputStream ->
                return asnInputStream.readObject() as ASN1Sequence
            }
        }
    }

    @Throws(WalletCoreException::class)
    private fun extractRS(asn1Sequence: ASN1Sequence): Pair<BigInteger, BigInteger> {
        val asn1Encodables = asn1Sequence.toArray()
        if (asn1Encodables.size != 2) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SIGNABLE_CREATE_SIGNATURE)
        }
        val integerR = (asn1Encodables[0].toASN1Primitive() as ASN1Integer).value
        val integerS = (asn1Encodables[1].toASN1Primitive() as ASN1Integer).value
        return Pair(integerR, integerS)
    }

    private fun adjustS(s: BigInteger, curveName: String): BigInteger {
        val curveN = SECNamedCurves.getByName(curveName).n
        val halfCurveOrder = curveN.shiftRight(1)
        return if (s > halfCurveOrder) curveN.subtract(s) else s
    }

    private fun padTo32Bytes(value: BigInteger): ByteArray {
        val result = ByteArray(32)
        val byteArray = value.toByteArray()
        if (byteArray.size > 32) {
            System.arraycopy(byteArray, byteArray.size - 32, result, 0, 32)
        } else if (byteArray.size < 32) {
            System.arraycopy(byteArray, 0, result, 32 - byteArray.size, byteArray.size)
        } else {
            return byteArray
        }
        return result
    }
}