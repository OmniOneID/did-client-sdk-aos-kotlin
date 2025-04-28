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

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EcKeyPair(
    @SerializedName("ecType") @Expose var ecType: EcType? = null,
    @SerializedName("privateKey") @Expose var privateKey: String? = null,
    @SerializedName("publicKey") @Expose var publicKey: String? = null
) {
    constructor(keyId: String, ecType: EcType, storeType: Int, publicKey: String, privateKey: String) : this(
        ecType = ecType,
        publicKey = publicKey,
        privateKey = privateKey
    )

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
