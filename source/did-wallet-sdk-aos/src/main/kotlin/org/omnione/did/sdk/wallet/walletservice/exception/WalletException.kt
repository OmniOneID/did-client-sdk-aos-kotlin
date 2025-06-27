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

package org.omnione.did.sdk.wallet.walletservice.exception

class WalletException : Exception {

    val errorCode: String?
    val errMsg: String?
    val errorReason: String?

    constructor(walletErrorCode: WalletErrorCode) : super(
        "[${walletErrorCode.feature}${walletErrorCode.code}}] ${walletErrorCode.msg}"
    ) {
        this.errorCode = walletErrorCode.code
        this.errMsg = walletErrorCode.msg
        this.errorReason = null
    }

    constructor(walletErrorCode: WalletErrorCode, paramName: String) : super(
        "[${walletErrorCode.feature}${walletErrorCode.code}}] ${walletErrorCode.msg} $paramName"
    ) {
        this.errorCode = walletErrorCode.code
        this.errMsg = "${walletErrorCode.msg} $paramName"
        this.errorReason = null
    }

    constructor(walletErrorCode: WalletErrorCode, throwable: Throwable) : super(
        "[${walletErrorCode.feature}${walletErrorCode.code}}] ${walletErrorCode.msg}] , [Message] ${throwable.message}"
    ) {
        this.errorCode = walletErrorCode.code
        this.errMsg = throwable.message ?: ""
        this.errorReason = null
    }

    constructor(code: String) : super(
        "[ErrorCode ${WalletErrorCode.getEnumByCode(code).code}] , [Message] ${WalletErrorCode.getEnumByCode(code).msg}"
    ) {
        this.errorCode = WalletErrorCode.getEnumByCode(code).code
        this.errorReason = WalletErrorCode.getEnumByCode(code).msg
        this.errMsg = WalletErrorCode.getEnumByCode(code).msg
    }

    constructor(code: String, throwable: Throwable) : super(
        "[ErrorCode ${WalletErrorCode.getEnumByCode(code).code}] , [Message] ${WalletErrorCode.getEnumByCode(code).msg}", throwable
    ) {
        this.errorCode = WalletErrorCode.getEnumByCode(code).code
        this.errorReason = WalletErrorCode.getEnumByCode(code).msg
        this.errMsg = WalletErrorCode.getEnumByCode(code).msg
    }
}