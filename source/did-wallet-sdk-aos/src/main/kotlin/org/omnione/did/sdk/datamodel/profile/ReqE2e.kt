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

package org.omnione.did.sdk.datamodel.profile

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.enums.SymmetricCipherType
import org.omnione.did.sdk.datamodel.common.enums.SymmetricPaddingType
import org.omnione.did.sdk.datamodel.common.enums.EllipticCurveType
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class ReqE2e(
    @SerializedName("nonce")
    @Expose
    var nonce: String? = null,

    @SerializedName("curve")
    @Expose
    var curve: EllipticCurveType.ELLIPTIC_CURVE_TYPE? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("cipher")
    @Expose
    var cipher: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE? = null,

    @SerializedName("padding")
    @Expose
    var padding: SymmetricPaddingType.SYMMETRIC_PADDING_TYPE? = null,

    @SerializedName("proof")
    @Expose
    var proof: Proof? = null
) {
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return gson.toJson(this)
    }

    override fun toString(): String {
        return "ReqE2e(" +
                "nonce=$nonce, " +
                "curve=$curve, " +
                "publicKey=$publicKey, " +
                "cipher=$cipher, " +
                "padding=$padding, " +
                "proof=$proof)"
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): ReqE2e {
            val gson = Gson()
            return gson.fromJson(json, ReqE2e::class.java)
        }
    }
}
