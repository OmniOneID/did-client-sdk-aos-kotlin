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

package org.omnione.did.sdk.core.keymanager.datamodel

import org.omnione.did.sdk.datamodel.util.IntEnum

class KeyAccessMethod {

    enum class KEY_ACCESS_METHOD(val intValue: Int) : IntEnum {
        WALLET_NONE(0),
        WALLET_PIN(1),
        KEYSTORE_NONE(8),
        KEYSTORE_BIOMETRY(9);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): KEY_ACCESS_METHOD? =
                values().find { it.intValue == intValue }
        }

        override fun toString(): String = when (this) {
            WALLET_NONE -> "WALLET_NONE"
            WALLET_PIN -> "WALLET_PIN"
            KEYSTORE_NONE -> "KEYSTORE_NONE"
            KEYSTORE_BIOMETRY -> "KEYSTORE_BIOMETRY"
        }

    }
}