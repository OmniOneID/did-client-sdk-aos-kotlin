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

package org.omnione.did.sdk.datamodel.did

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.enums.AuthType
import org.omnione.did.sdk.datamodel.did.DIDKeyType
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class VerificationMethod(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: DIDKeyType.DID_KEY_TYPE? = null,

    @SerializedName("controller")
    @Expose
    var controller: String? = null,

    @SerializedName("publicKeyMultibase")
    @Expose
    var publicKeyMultibase: String? = null,

    @SerializedName("authType")
    @Expose
    var authType: AuthType.AUTH_TYPE? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null
) {
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .create()

        return gson.toJson(this)
    }

    override fun toString(): String {
        return "VerificationMethod(id=$id, type=$type, controller=$controller, publicKeyMultibase=$publicKeyMultibase, authType=$authType, status=$status)"
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): VerificationMethod {
            val gson = Gson()
            return gson.fromJson(json, VerificationMethod::class.java)
        }
    }
}
