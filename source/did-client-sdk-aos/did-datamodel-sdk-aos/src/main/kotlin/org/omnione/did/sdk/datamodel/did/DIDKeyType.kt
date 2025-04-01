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

import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType;
import org.omnione.did.sdk.datamodel.util.StringEnum;

class DIDKeyType {
    enum class DID_KEY_TYPE(private val value: String) : StringEnum {
        rsaVerificationKey2018("RsaVerificationKey2018"),
        secp256k1VerificationKey2018("Secp256k1VerificationKey2018"),
        secp256r1VerificationKey2018("Secp256r1VerificationKey2018");

        override fun getStringValue(): String {
            return value
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): DID_KEY_TYPE? {
                return values().find { it.value == value }
            }

            @JvmStatic
            fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): DID_KEY_TYPE {
                return when (type) {
                    AlgorithmType.ALGORITHM_TYPE.RSA -> rsaVerificationKey2018
                    AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> secp256k1VerificationKey2018
                    AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> secp256r1VerificationKey2018
                    else -> throw RuntimeException("Unsupported algorithm type")
                }
            }

            @JvmStatic
            fun convertTo(type: DID_KEY_TYPE): AlgorithmType.ALGORITHM_TYPE {
                return when (type) {
                    rsaVerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.RSA
                    secp256k1VerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
                    secp256r1VerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
                    else -> throw RuntimeException("Unsupported DID key type")
                }
            }
        }

        override fun toString(): String {
            return when (this) {
                rsaVerificationKey2018 -> "RsaVerificationKey2018"
                secp256k1VerificationKey2018 -> "Secp256k1VerificationKey2018"
                secp256r1VerificationKey2018 -> "Secp256r1VerificationKey2018"
                else -> "Unknown"
            }
        }
    }
}
