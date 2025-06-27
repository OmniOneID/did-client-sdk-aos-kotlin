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

package org.omnione.did.sdk.datamodel.vc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.BaseObject
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class VerifiableCredential @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String> = mutableListOf(),

    @SerializedName("id")
    @Expose
    var id: String = "",

    @SerializedName("type")
    @Expose
    var type: List<String> = mutableListOf(),

    @SerializedName("issuer")
    @Expose
    var issuer: Issuer = Issuer(),

    @SerializedName("issuanceDate")
    @Expose
    var issuanceDate: String = "",

    @SerializedName("validFrom")
    @Expose
    var validFrom: String = "", // UTC date time

    @SerializedName("validUntil")
    @Expose
    var validUntil: String = "", // UTC date time

    @SerializedName("encoding")
    @Expose
    var encoding: String? = null,

    @SerializedName("formatVersion")
    @Expose
    var formatVersion: String? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("evidence")
    @Expose
    var evidence: List<Evidence>? = null,

    @SerializedName("credentialSchema")
    @Expose
    var credentialSchema: CredentialSchema = CredentialSchema(),

    @SerializedName("credentialSubject")
    @Expose
    var credentialSubject: CredentialSubject = CredentialSubject(),

    @SerializedName("proof")
    @Expose
    var proof: VCProof = VCProof()
) : BaseObject() {

    override fun fromJson(json: String) {
        val gson = Gson()
        val obj = gson.fromJson(json, VerifiableCredential::class.java)

        context = obj.context
        id = obj.id
        type = obj.type
        issuer = obj.issuer
        issuanceDate = obj.issuanceDate
        validFrom = obj.validFrom
        validUntil = obj.validUntil
        encoding = obj.encoding
        formatVersion = obj.formatVersion
        language = obj.language
        evidence = obj.evidence
        credentialSchema = obj.credentialSchema
        credentialSubject = obj.credentialSubject
        proof = obj.proof
    }

    override fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()

        val json = gson.toJson(this)
        return JsonSortUtil.sortJsonString(gson, json)
    }
}
