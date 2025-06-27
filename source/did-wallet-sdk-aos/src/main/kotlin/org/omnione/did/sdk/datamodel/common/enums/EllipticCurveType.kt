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

class EllipticCurveType {
    enum class ELLIPTIC_CURVE_TYPE(val value: String) : StringEnum {
        SECP256K1("Secp256k1"),
        SECP256R1("Secp256r1");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            SECP256K1 -> "Secp256k1"
            SECP256R1 -> "Secp256r1"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): ELLIPTIC_CURVE_TYPE? = 
                values().find { it.value == value }

            @JvmStatic
            fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): ELLIPTIC_CURVE_TYPE = when (type) {
                AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> SECP256K1
                AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> SECP256R1
                else -> throw RuntimeException()
            }

            @JvmStatic
            fun convertTo(type: ELLIPTIC_CURVE_TYPE): AlgorithmType.ALGORITHM_TYPE = when (type) {
                SECP256K1 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
                SECP256R1 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
            }
        }
    }
}