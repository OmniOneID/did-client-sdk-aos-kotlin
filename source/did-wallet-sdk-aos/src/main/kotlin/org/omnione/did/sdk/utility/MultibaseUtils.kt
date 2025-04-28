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

import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.Encodings.Base16
import org.omnione.did.sdk.utility.Encodings.Base16Upper
import org.omnione.did.sdk.utility.Encodings.Base64
import org.omnione.did.sdk.utility.Errors.UtilityErrorCode
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.bitcoinj.core.Base58

object MultibaseUtils {

    /**
     * Multibase encoding.
     *
     * This method encodes the given byte array into a Multibase string using the specified Multibase type.
     *
     * @param type The Multibase type to use for encoding.
     * @param data The byte array to encode.
     * @return The Multibase encoded string.
     */
    @JvmStatic
    fun encode(type: MultibaseType.MULTIBASE_TYPE, data: ByteArray?): String = data?.let {
        StringBuilder(type.value).apply {
            when (type) {
                MultibaseType.MULTIBASE_TYPE.BASE_16 -> append(bytesToHex(it))
                MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER -> append(bytesToHexUpper(it))
                MultibaseType.MULTIBASE_TYPE.BASE_58_BTC -> append(encodeBase58(it))
                MultibaseType.MULTIBASE_TYPE.BASE_64 -> append(encodeBase64(it))
                MultibaseType.MULTIBASE_TYPE.BASE_64_URL -> append(encodeBase64URL(it))
            }
        }.toString()
    } ?: ""

    /**
     * Multibase decoding.
     *
     * This method decodes the given Multibase encoded string back into a byte array.
     *
     * @param encoded The Multibase encoded string to decode.
     * @return The decoded byte array.
     * @throws Exception If the input string is invalid or the decoding fails.
     */
    @JvmStatic
    @Throws(UtilityException::class)
    fun decode(encoded: String): ByteArray = when {
        encoded.length < 2 -> throw UtilityException(UtilityErrorCode.ERR_CODE_MULTIBASE_UTILS_INVALID_PARAMETER, "encoded")
        else -> encoded.let {
            val firstString = it.substring(0, 1)
            val remainString = it.substring(1)

            getMultibaseEnum(firstString)?.let { type ->
                when (type) {
                    MultibaseType.MULTIBASE_TYPE.BASE_16 -> hexToBytes(remainString)
                    MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER -> hexUpperToBytes(remainString)
                    MultibaseType.MULTIBASE_TYPE.BASE_58_BTC -> decodeBase58(remainString)
                    MultibaseType.MULTIBASE_TYPE.BASE_64 -> decodeBase64(remainString)
                    MultibaseType.MULTIBASE_TYPE.BASE_64_URL -> decodeBase64URL(remainString)
                }
            } ?: throw UtilityException(UtilityErrorCode.ERR_CODE_MULTIBASE_UTILS_FAIL_TO_DECODE)
        }
    }

    private fun bytesToHex(bytes: ByteArray): String = Base16.toHex(bytes)

    private fun hexToBytes(hex: String): ByteArray = Base16.toBytes(hex)

    private fun bytesToHexUpper(bytes: ByteArray): String = Base16Upper.toHex(bytes).uppercase()

    private fun hexUpperToBytes(hex: String): ByteArray = Base16Upper.toBytes(hex)

    private fun encodeBase58(source: ByteArray): String = Base58.encode(source)

    private fun decodeBase58(base58: String): ByteArray = Base58.decode(base58)

    private fun encodeBase64(source: ByteArray): String = Base64.encodeToString(source, Base64.NO_WRAP)

    private fun decodeBase64(base64: String): ByteArray = Base64.decode(base64, Base64.NO_WRAP)

    private fun encodeBase64URL(source: ByteArray): String = Base64.encodeUrlString(source)

    private fun decodeBase64URL(base64url: String): ByteArray = Base64.decodeUrl(base64url)

    private fun getMultibaseEnum(value: String): MultibaseType.MULTIBASE_TYPE? =
        MultibaseType.MULTIBASE_TYPE.entries.firstOrNull { it.value == value }
}