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

package org.omnione.did.sdk.datamodel.vcschema

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.enums.ClaimFormat
import org.omnione.did.sdk.datamodel.common.enums.ClaimType
import org.omnione.did.sdk.datamodel.common.enums.Location
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class VCSchema(
    @SerializedName("@id")
    @Expose
    var id: String? = null,

    @SerializedName("@schema")
    @Expose
    var schema: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("metadata")
    @Expose
    var metadata: VCMetadata? = null,

    @SerializedName("credentialSubject")
    @Expose
    var credentialSubject: CredentialSubject? = null
) {
    data class VCMetadata(
        @JvmField
        var language: String? = null,
        @JvmField
        var formatVersion: String? = null
    )

    data class CredentialSubject(
        @JvmField
        var claims: List<Claim>? = null
    ) {
        fun getClaims() : List<Claim>? {
            return claims;
        }
    }

    data class Claim(
        @JvmField
        var namespace: Namespace? = null,
        @JvmField
        var items: List<ClaimDef>? = null
    )

    data class Namespace(
        @JvmField
        var id: String? = null,
        @JvmField
        var name: String? = null,
        @JvmField
        var ref: String? = null
    )

    data class ClaimDef(
        @JvmField
        var id: String? = null,
        @JvmField
        var caption: String? = null,
        @JvmField
        var type: ClaimType.CLAIM_TYPE? = null,
        @JvmField
        var format: ClaimFormat.CLAIM_FORMAT? = null,
        @JvmField
        var hideValue: Boolean = false,
        @JvmField
        var location: Location.LOCATION? = null,
        @JvmField
        var required: Boolean = true,
        @JvmField
        var description: String = "",
        @JvmField
        var i18n: Map<String, String>? = null
    )

    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()

        return gson.toJson(this)
    }
}
