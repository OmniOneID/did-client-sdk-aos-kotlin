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

import org.omnione.did.sdk.datamodel.did.DIDKeyType;
import org.omnione.did.sdk.datamodel.util.StringEnum;

class AlgorithmType {
    enum class ALGORITHM_TYPE(val value: String) : StringEnum {
        RSA("Rsa"),
        SECP256K1("Secp256k1"),
        SECP256R1("Secp256r1");

//        override fun getValue(): String {
//            return value
//        }

        override fun getStringValue(): String = value

        override fun toString(): String = when (this) {
            RSA -> "Rsa"
            SECP256K1 -> "Secp256k1"
            SECP256R1 -> "Secp256r1"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): ALGORITHM_TYPE? = 
                values().find { it.value == value }

            @JvmStatic
            fun convertTo(type: ALGORITHM_TYPE): DIDKeyType.DID_KEY_TYPE = when (type) {
                RSA -> DIDKeyType.DID_KEY_TYPE.rsaVerificationKey2018
                SECP256K1 -> DIDKeyType.DID_KEY_TYPE.secp256k1VerificationKey2018
                SECP256R1 -> DIDKeyType.DID_KEY_TYPE.secp256r1VerificationKey2018
            }
        }
    }
}
