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

package org.omnione.did.sdk.utility.DataModels

class EcType {
    enum class EC_TYPE(val value: String) {
        SECP256_K1("Secp256k1"),
        SECP256_R1("Secp256r1");

        companion object {
            @JvmStatic
            fun fromValue(value: String): EC_TYPE? {
                return values().find { it.value == value }
            }
        }

        override fun toString(): String {
            return when (this) {
                SECP256_K1 -> "Secp256k1"
                SECP256_R1 -> "Secp256r1"
            }
        }
    }
}
