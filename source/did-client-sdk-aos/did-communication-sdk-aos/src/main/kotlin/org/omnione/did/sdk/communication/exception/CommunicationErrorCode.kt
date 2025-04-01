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

enum class CommunicationErrorCode(val feature: String, val code: String, val msg: String) {

    /* COMMON ERROR */
    // ModuleCode(1) + StackCode(3) + ComponentCode(3) + ErrorCode(5)
    // ModuleCode - Mobile (M)
    // StackCode - SDK (SDK)
    // ComponentCode - Communication (CMM)

    ERR_CODE_COMMUNICATION_UNKNOWN("MSDKCMM", "00000", "unknown error"),
    ERR_CODE_COMMUNICATION_INVALID_PARAMETER("MSDKCMM", "00001", "Invalid parameter : "),
    ERR_CODE_COMMUNICATION_INCORRECT_URL_CONNECTION("MSDKCMM", "00002", "Incorrect url connection : "),
    ERR_CODE_COMMUNICATION_SERVER_FAIL("MSDKCMM", "00003", "ServerFail : ");

    companion object {
        @JvmStatic
        fun getEnumByCode(code: String): CommunicationErrorCode {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown Enum Code: $code")
        }
    }
}
