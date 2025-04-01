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

class UtilityException : Exception {

    val errorCode: String?
    val errMsg: String?
    val errorReason: String?

    constructor(utilityErrorCode: UtilityErrorCode) : super(
        "[${utilityErrorCode.feature}${utilityErrorCode.code}}] ${utilityErrorCode.msg}"
    ) {
        this.errorCode = utilityErrorCode.code
        this.errMsg = utilityErrorCode.msg
        this.errorReason = null
    }

    constructor(utilityErrorCode: UtilityErrorCode, paramName: String) : super(
        "[${utilityErrorCode.feature}${utilityErrorCode.code}}] ${utilityErrorCode.msg} $paramName"
    ) {
        this.errorCode = utilityErrorCode.code
        this.errMsg = "${utilityErrorCode.msg} $paramName"
        this.errorReason = null
    }

    constructor(utilityErrorCode: UtilityErrorCode, throwable: Throwable) : super(
        "[${utilityErrorCode.feature}${utilityErrorCode.code}}] ${utilityErrorCode.msg}] , [Message] ${throwable.message}"
    ) {
        this.errorCode = utilityErrorCode.code
        this.errMsg = throwable.message ?: ""
        this.errorReason = null
    }

    constructor(code: String) : super(
        "[ErrorCode ${UtilityErrorCode.getEnumByCode(code).code}] , [Message] ${UtilityErrorCode.getEnumByCode(code).msg}"
    ) {
        this.errorCode = UtilityErrorCode.getEnumByCode(code).code
        this.errorReason = UtilityErrorCode.getEnumByCode(code).msg
        this.errMsg = UtilityErrorCode.getEnumByCode(code).msg
    }

    constructor(code: String, throwable: Throwable) : super(
        "[ErrorCode ${UtilityErrorCode.getEnumByCode(code).code}] , [Message] ${UtilityErrorCode.getEnumByCode(code).msg}", throwable
    ) {
        this.errorCode = UtilityErrorCode.getEnumByCode(code).code
        this.errorReason = UtilityErrorCode.getEnumByCode(code).msg
        this.errMsg = UtilityErrorCode.getEnumByCode(code).msg
    }
}
