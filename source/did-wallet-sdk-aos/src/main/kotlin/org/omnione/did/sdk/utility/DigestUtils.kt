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

import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.Errors.UtilityErrorCode
import org.omnione.did.sdk.utility.Errors.UtilityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.CompletionException

object DigestUtils {

    /**
     * Generates a hash of the given byte array using the specified digest algorithm.
     * Supports SHA-256, SHA-384, and SHA-512.
     *
     * @param source The input byte array to be hashed.
     * @param digestEnum The digest algorithm to use.
     * @return The hashed byte array.
     * @throws UtilityException If the input is null or an unsupported algorithm is provided.
     */
    @JvmStatic
    @Throws(UtilityException::class)
    fun getDigest(source: ByteArray?, digestEnum: DigestEnum.DIGEST_ENUM?): ByteArray = source?.let {
        digestEnum?.let { algorithm ->
            when (algorithm) {
                DigestEnum.DIGEST_ENUM.SHA_256 -> getShaDigest(it, "SHA-256")
                DigestEnum.DIGEST_ENUM.SHA_384 -> getShaDigest(it, "SHA-384")
                DigestEnum.DIGEST_ENUM.SHA_512 -> getShaDigest(it, "SHA-512")
                else -> throw UtilityException(
                    UtilityErrorCode.ERR_CODE_DIGEST_UTILS_UNSUPPORTED_ALGORITHM_TYPE,
                    algorithm.value
                )
            }
        } ?: throw UtilityException(
            UtilityErrorCode.ERR_CODE_DIGEST_UTILS_UNSUPPORTED_ALGORITHM_TYPE,
            "Unknown"
        )
    } ?: throw UtilityException(UtilityErrorCode.ERR_CODE_DIGEST_UTILS_INVALID_PARAMETER, "source")

    /**
     * Computes the SHA digest for the given input byte array using the specified algorithm.
     *
     * @param source The input byte array to be hashed.
     * @param algorithm The SHA algorithm name (e.g., "SHA-256").
     * @return The hashed byte array.
     * @throws UtilityException If the algorithm is not supported.
     */
    @JvmStatic
    @Throws(UtilityException::class)
    private fun getShaDigest(source: ByteArray, algorithm: String): ByteArray = runCatching {
        MessageDigest.getInstance(algorithm).let { digest ->
            digest.digest(source)
        }
    }.getOrElse { e ->
        when (e) {
            is NoSuchAlgorithmException -> throw UtilityException(UtilityErrorCode.ERR_CODE_DIGEST_UTILS_UNSUPPORTED_ALGORITHM_TYPE, algorithm)
            else -> throw e
        }
    }
}