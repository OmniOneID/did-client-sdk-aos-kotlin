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

class StorageOption {
    enum class STORAGE_OPTION(val intValue: Int) : IntEnum {
        WALLET(0),
        KEYSTORE(1);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(value: Int): STORAGE_OPTION? =
                values().find { it.intValue == value }
        }

        override fun toString(): String = when (this) {
            WALLET -> "WALLET"
            KEYSTORE -> "KEYSTORE"
        }
    }
}
