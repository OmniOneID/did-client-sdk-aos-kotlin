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

package org.omnione.did.sdk.datamodel.common.enums

import org.omnione.did.sdk.datamodel.util.IntEnum

class WalletTokenPurpose {

    enum class WALLET_TOKEN_PURPOSE(val intValue: Int) : IntEnum {
        PERSONALIZE(1),
        DEPERSONALIZE(2),
        PERSONALIZE_AND_CONFIGLOCK(3),
        CONFIGLOCK(4),
        CREATE_DID(5),
        UPDATE_DID(6),
        RESTORE_DID(7),
        ISSUE_VC(8),
        REMOVE_VC(9),
        PRESENT_VP(10),
        LIST_VC(11),
        DETAIL_VC(12),
        CREATE_DID_AND_ISSUE_VC(13),
        LIST_VC_AND_PRESENT_VP(14);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): WALLET_TOKEN_PURPOSE? =
                values().find { it.intValue == intValue }
        }

        override fun toString(): String = when (this) {
            PERSONALIZE -> "Personalize"
            DEPERSONALIZE -> "Depersonalize"
            PERSONALIZE_AND_CONFIGLOCK -> "PersonalizeAndConfigLock"
            CONFIGLOCK -> "ConfigLock"
            CREATE_DID -> "CreateDID"
            UPDATE_DID -> "UpdateDID"
            RESTORE_DID -> "RestoreDID"
            ISSUE_VC -> "IssueVc"
            REMOVE_VC -> "RemoveVc"
            PRESENT_VP -> "PresentVp"
            LIST_VC -> "ListVc"
            DETAIL_VC -> "DetailVc"
            CREATE_DID_AND_ISSUE_VC -> "CreateDIDAndIssueVc"
            LIST_VC_AND_PRESENT_VP -> "ListVCAndPresentVP"
        }
    }
}

