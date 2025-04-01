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
import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
import org.omnione.did.sdk.core.exception.WalletCoreException

object SecureEncryptor {
    private const val SECURE_ENCRYPTOR_ALIAS_PREFIX = "opendid_wallet_encryption_"
    private const val SECURE_ENCRYPTOR_ALIAS = "secureEncryptor"
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun encrypt(plainData: ByteArray?, context: Context?): ByteArray {
        if (plainData == null || plainData.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SECURE_ENCRYPTOR_INVALID_PARAMETER, "plainData")
        }
        if (context == null) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SECURE_ENCRYPTOR_INVALID_PARAMETER, "context")
        }
        return KeystoreManager.encrypt(SECURE_ENCRYPTOR_ALIAS_PREFIX + SECURE_ENCRYPTOR_ALIAS, plainData, context)
    }
    
    @JvmStatic
    @Throws(WalletCoreException::class)
    fun decrypt(cipherData: ByteArray?): ByteArray {
        if (cipherData == null || cipherData.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_SECURE_ENCRYPTOR_INVALID_PARAMETER, "cipherData")
        }
        return KeystoreManager.decrypt(SECURE_ENCRYPTOR_ALIAS_PREFIX + SECURE_ENCRYPTOR_ALIAS, cipherData)
    }
}
