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

package org.omnione.did.sdk.core.exception

class WalletCoreException : Exception {

    val errorCode: String?
    val errMsg: String?
    val errorReason: String?

    constructor(walletCoreErrorCode: WalletCoreErrorCode) : super(
        "[${walletCoreErrorCode.feature}${walletCoreErrorCode.code}] ${walletCoreErrorCode.msg}"
    ) {
        this.errorCode = walletCoreErrorCode.code
        this.errMsg = walletCoreErrorCode.msg
        this.errorReason = null
    }

    constructor(walletCoreErrorCode: WalletCoreErrorCode, paramName: String) : super(
        "[${walletCoreErrorCode.feature}${walletCoreErrorCode.code}] ${walletCoreErrorCode.msg} $paramName"
    ) {
        this.errorCode = walletCoreErrorCode.code
        this.errMsg = "${walletCoreErrorCode.msg} $paramName"
        this.errorReason = null
    }

    constructor(walletCoreErrorCode: WalletCoreErrorCode, throwable: Throwable) : super(
        "[${walletCoreErrorCode.feature}${walletCoreErrorCode.code}], [Message] ${throwable.message}",
        throwable
    ) {
        this.errorCode = walletCoreErrorCode.code
        this.errMsg = throwable.message
        this.errorReason = null
    }

    constructor(code: String) : this(WalletCoreErrorCode.getEnumByCode(code))

    constructor(code: String, throwable: Throwable) : this(WalletCoreErrorCode.getEnumByCode(code), throwable)
}
