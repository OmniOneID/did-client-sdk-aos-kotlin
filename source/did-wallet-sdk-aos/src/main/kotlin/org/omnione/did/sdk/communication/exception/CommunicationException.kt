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

package org.omnione.did.sdk.communication.exception

class CommunicationException : Exception {

    val errorCode: String?
    val errMsg: String?
    val errorReason: String?

    constructor(communicationErrorCode: CommunicationErrorCode) : super(
        "[${communicationErrorCode.feature}${communicationErrorCode.code}] ${communicationErrorCode.msg}"
    ) {
        this.errorCode = communicationErrorCode.code
        this.errMsg = communicationErrorCode.msg
        this.errorReason = null
    }

    constructor(communicationErrorCode: CommunicationErrorCode, paramName: String) : super(
        "[${communicationErrorCode.feature}${communicationErrorCode.code}] ${communicationErrorCode.msg} $paramName"
    ) {
        this.errorCode = communicationErrorCode.code
        this.errMsg = "${communicationErrorCode.msg} $paramName"
        this.errorReason = null
    }

    constructor(communicationErrorCode: CommunicationErrorCode, throwable: Throwable) : super(
        "[${communicationErrorCode.feature}${communicationErrorCode.code}], [Message] ${throwable.message}",
        throwable
    ) {
        this.errorCode = communicationErrorCode.code
        this.errMsg = throwable.message
        this.errorReason = null
    }

    constructor(code: String) : this(CommunicationErrorCode.getEnumByCode(code))

    constructor(code: String, throwable: Throwable) : this(CommunicationErrorCode.getEnumByCode(code), throwable)
}
