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

package org.omnione.did.sdk.core.didmanager.datamodel

import org.omnione.did.sdk.datamodel.util.IntEnum

class DIDMethodType {
    enum class DID_METHOD_TYPE(val intValue: Int) : IntEnum {
        assertionMethod(1),
        authentication(1 shl 1),
        keyAgreement(1 shl 2),
        capabilityInvocation(1 shl 3),
        capabilityDelegation(1 shl 4);

        override fun getValue(): Int {
            return intValue
        }

        companion object {
            @JvmStatic
            fun fromValue(intValue: Int): DID_METHOD_TYPE? =
                values().find { it.intValue == intValue }
        }

        override fun toString(): String = when (this) {
            assertionMethod -> "assertionMethod"
            authentication -> "authentication"
            keyAgreement -> "keyAgreement"
            capabilityInvocation -> "capabilityInvocation"
            capabilityDelegation -> "capabilityDelegation"
        }
    }
}