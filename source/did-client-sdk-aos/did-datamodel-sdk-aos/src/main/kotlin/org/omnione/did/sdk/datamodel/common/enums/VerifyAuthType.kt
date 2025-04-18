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

class VerifyAuthType {

    enum class VERIFY_AUTH_TYPE(val intValue: Int) : IntEnum {
        ANY(0x00000000),
        FREE(0x00000001),
        PIN(0x00000002),
        BIO(0x00000004),
        PIN_OR_BIO(0x00000006),
        PIN_AND_BIO(0x00008006);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): VERIFY_AUTH_TYPE? =
                values().find { it.intValue == intValue }
        }
    }
}