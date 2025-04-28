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

import org.omnione.did.sdk.datamodel.util.StringEnum;

class SymmetricCipherType {

    enum class SYMMETRIC_CIPHER_TYPE(val value: String) : StringEnum {
        AES128CBC("AES-128-CBC"),
        AES128ECB("AES-128-ECB"),
        AES256CBC("AES-256-CBC"),
        AES256ECB("AES-256-ECB");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            AES128CBC -> "AES-128-CBC"
            AES128ECB -> "AES-128-ECB"
            AES256CBC -> "AES-256-CBC"
            AES256ECB -> "AES-256-ECB"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): SYMMETRIC_CIPHER_TYPE? =
                values().find { it.value == value }
        }
    }
}
