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

package org.omnione.did.sdk.datamodel.profile;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class ProviderDetail(
    @SerializedName("did")
    @Expose
    var did: String? = null,

    @SerializedName("certVcRef")
    @Expose
    var certVcRef: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("ref")
    @Expose
    var ref: String? = null
) {
    fun getDID() : String? {
        return did;
    }
    fun setDID(did: String?) {
        this.did = did;
    }
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return gson.toJson(this)
    }

    override fun toString(): String {
        return "ProviderDetail(" +
                "did=$did, " +
                "certVcRef=$certVcRef, " +
                "name=$name, " +
                "description=$description, " +
                "logo=$logo, " +
                "ref=$ref)"
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): ProviderDetail {
            val gson = Gson()
            return gson.fromJson(json, ProviderDetail::class.java)
        }
    }
}

