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
import org.omnione.did.sdk.datamodel.common.BaseObject
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

class DIDDocument @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String> = listOf("https://www.w3.org/ns/did/v1"),

    @SerializedName("id")
    @Expose
    var id: String = "",

    @SerializedName("controller")
    @Expose
    var controller: String? = null,

    @SerializedName("verificationMethod")
    @Expose
    var verificationMethod: List<VerificationMethod>? = null,

    @SerializedName("assertionMethod")
    @Expose
    var assertionMethod: List<String>? = null,

    @SerializedName("authentication")
    @Expose
    var authentication: List<String>? = null,

    @SerializedName("keyAgreement")
    @Expose
    var keyAgreement: List<String>? = null,

    @SerializedName("capabilityInvocation")
    @Expose
    var capabilityInvocation: List<String>? = null,

    @SerializedName("capabilityDelegation")
    @Expose
    var capabilityDelegation: List<String>? = null,

    @SerializedName("service")
    @Expose
    var service: List<Service>? = null,

    @SerializedName("created")
    @Expose
    var created: String? = null,

    @SerializedName("updated")
    @Expose
    var updated: String? = null,

    @SerializedName("versionId")
    @Expose
    var versionId: String = "1",

    @SerializedName("deactivated")
    @Expose
    var deactivated: Boolean = false,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : BaseObject(), ProofContainer {

        constructor(id: String, controller: String?, created: String) : this(
            context = listOf("https://www.w3.org/ns/did/v1"),
            id = id,
            controller = controller ?: id,
            created = created,
            updated = created,
            versionId = "1",
            deactivated = false
        )

        override fun fromJson(json: String) {
        Gson().fromJson(json, DIDDocument::class.java)
    }

    override fun getProof(): Proof? {
        return _proof
    }

    override fun setProof(proof: Proof?) {
        this._proof = proof
    }

    override fun getProofs(): List<Proof>? {
        return _proofs
    }

    override fun setProofs(proofs: List<Proof>) {
        this._proofs = proofs
    }

    override fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return JsonSortUtil.sortJsonString(gson, gson.toJson(this))
    }

    override fun toString(): String {
        return "DIDDocument(id=$id, controller=$controller, created=$created, updated=$updated, versionId=$versionId, deactivated=$deactivated)"
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): DIDDocument {
            return Gson().fromJson(json, DIDDocument::class.java)
        }
    }
}