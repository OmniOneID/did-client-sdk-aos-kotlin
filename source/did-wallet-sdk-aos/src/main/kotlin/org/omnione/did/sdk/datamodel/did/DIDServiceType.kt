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

package org.omnione.did.sdk.datamodel.did;

import org.omnione.did.sdk.datamodel.util.StringEnum;

class DIDServiceType {
    enum class DID_SERVICE_TYPE(val value: String) : StringEnum {
        linkedDomains("LinkedDomains"),
        credentialRegistry("CredentialRegistry");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            linkedDomains -> "LinkedDomains"
            credentialRegistry -> "CredentialRegistry"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): DID_SERVICE_TYPE? =
                values().find { it.value == value }
        }
    }
}
