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

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.enums.AlgorithmType

data class KeyGenerationInfo(
    @SerializedName("algoType")
    var algoType: AlgorithmType.ALGORITHM_TYPE = AlgorithmType.ALGORITHM_TYPE.SECP256R1,

    @SerializedName("publicKey")
    var publicKey: String = "",

    @SerializedName("privateKey")
    var privateKey: String = ""
) {
    fun toJson(): String = Gson().toJson(this)
}
