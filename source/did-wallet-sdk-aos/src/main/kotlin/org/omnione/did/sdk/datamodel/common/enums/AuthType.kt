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

class AuthType {
    enum class AUTH_TYPE(val intValue: Int) : IntEnum {
        FREE(1),
        PIN(2),
        BIO(4);

        override fun getValue(): Int {
            return intValue
        }

        override fun toString(): String = when (this) {
            FREE -> "무인증"
            PIN -> "비밀번호 인증"
            BIO -> "생체인증"
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): AUTH_TYPE? =
                values().find { it.intValue == intValue }
        }
    }
}
