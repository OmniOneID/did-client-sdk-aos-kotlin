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

package org.omnione.did.sdk.utility.Errors

enum class UtilityErrorCode(val feature: String, val code: String, val msg: String) {

    /* COMMON ERROR */
    // ModuleCode(1) + StackCode(3) + ComponentCode(3) + ErrorCode(5)
    // ModuleCode - Mobile (M)
    // StackCode - SDK (SDK)
    // ComponentCode - Utility (UTL)

    ERR_CODE_CRYPTO_UTILS_INVALID_PARAMETER("MSDKUTL", "00000", "Invalid parameter : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_CREATE_RANDOM_KEY("MSDKUTL", "00100", "Fail to create random key : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_DERIVE_PUBLIC_KEY("MSDKUTL", "00101", "Fail to derive public key"),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_GENERATE_SHARED_SECRET_USING_ECDH("MSDKUTL", "00102", "Fail to generate shared secret using ECDH : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_DERIVE_KEY_USING_PBKDF2("MSDKUTL", "00103", "Fail to derive key using PBKDF2"),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_CONVERT_PUBLIC_KEY_TO_EXTERNAL_REPRESENTATION("MSDKUTL", "00200", "Fail to convert public key to external representation : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_CONVERT_PRIVATE_KEY_TO_EXTERNAL_REPRESENTATION("MSDKUTL", "00201", "Fail to convert private key to external representation : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_CONVERT_PRIVATE_KEY_TO_OBJECT("MSDKUTL", "00202", "Fail to convert private key to object : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_CONVERT_PUBLIC_KEY_TO_OBJECT("MSDKUTL", "00203", "Fail to convert public key to object : "),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_ENCRYPT_USING_AES("MSDKUTL", "00300", "Fail to encrypt using AES"),
    ERR_CODE_CRYPTO_UTILS_FAIL_TO_DECRYPT_USING_AES("MSDKUTL", "00301", "Fail to decrypt using AES"),
    ERR_CODE_CRYPTO_UTILS_UNSUPPORTED_EC_TYPE("MSDKUTL", "00900", "Unsupported ECType : "),

    ERR_CODE_MULTIBASE_UTILS_INVALID_PARAMETER("MSDKUTL", "01000", "Invalid parameter : "),
    ERR_CODE_MULTIBASE_UTILS_FAIL_TO_DECODE("MSDKUTL", "01100", "Fail to decode"),
    ERR_CODE_MULTIBASE_UTILS_UNSUPPORTED_ENCODING_TYPE("MSDKUTL", "01900", "Unsupported encoding type : "),

    ERR_CODE_DIGEST_UTILS_INVALID_PARAMETER("MSDKUTL", "02000", "Invalid parameter : "),
    ERR_CODE_DIGEST_UTILS_UNSUPPORTED_ALGORITHM_TYPE("MSDKUTL","02900", "Unsupported algorithm type : ");

    companion object {
        @JvmStatic
        fun getEnumByCode(code: String): UtilityErrorCode {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown Enum Code: $code")
        }
    }
}
