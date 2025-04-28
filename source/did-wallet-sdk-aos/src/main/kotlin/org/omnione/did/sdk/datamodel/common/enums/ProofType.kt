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

import org.omnione.did.sdk.datamodel.did.DIDKeyType
import org.omnione.did.sdk.datamodel.util.StringEnum;

class ProofType {
    enum class PROOF_TYPE(val value: String) : StringEnum {
        rsaSignature2018("RsaSignature2018"),
        secp256k1Signature2018("Secp256k1Signature2018"),
        secp256r1Signature2018("Secp256r1Signature2018");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            rsaSignature2018 -> "RsaSignature2018"
            secp256k1Signature2018 -> "Secp256k1Signature2018"
            secp256r1Signature2018 -> "Secp256r1Signature2018"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): PROOF_TYPE? =
                values().find { it.value == value }

            @JvmStatic
            fun convertTo(type: PROOF_TYPE): AlgorithmType.ALGORITHM_TYPE = when (type) {
                rsaSignature2018 -> AlgorithmType.ALGORITHM_TYPE.RSA
                secp256k1Signature2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
                secp256r1Signature2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
            }

            @JvmStatic
            fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): PROOF_TYPE = when (type) {
                AlgorithmType.ALGORITHM_TYPE.RSA -> rsaSignature2018
                AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> secp256k1Signature2018
                AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> secp256r1Signature2018
            }
        }
    }
}
