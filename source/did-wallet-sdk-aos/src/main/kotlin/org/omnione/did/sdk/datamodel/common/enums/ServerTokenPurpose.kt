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

package org.omnione.did.sdk.datamodel.common.enums;

import org.omnione.did.sdk.datamodel.util.IntEnum;

class ServerTokenPurpose {

    enum class SERVER_TOKEN_PURPOSE(val intValue: Int) : IntEnum {
        CREATE_DID(5),
        UPDATE_DID(6),
        RESTORE_DID(7),
        ISSUE_VC(8),
        REVOKE_VC(9),
        PRESENT_VP(10),
        CREATE_DID_AND_ISSUE_VC(13);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): ServerTokenPurpose.SERVER_TOKEN_PURPOSE? =
                ServerTokenPurpose.SERVER_TOKEN_PURPOSE.values().find { it.intValue == intValue }
        }

        override fun toString(): String = when (this) {
            CREATE_DID -> "CreateDID"
            UPDATE_DID -> "UpdateDID";
            RESTORE_DID -> "RestoreDID";
            ISSUE_VC -> "IssueVc";
            REVOKE_VC -> "RevokeVc";
            PRESENT_VP -> "PresentVp";
            CREATE_DID_AND_ISSUE_VC -> "CreateDIDAndIssueVc";
        }
    }
}
