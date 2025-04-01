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
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory
import org.omnione.did.sdk.datamodel.common.enums.CredentialSchemaType

class IssueProfile(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: ProfileType.PROFILE_TYPE? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("encoding")
    @Expose
    var encoding: String? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("profile")
    @Expose
    var profile: Profile? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null,
) : ProofContainer {

    data class Profile(
        @JvmField
        var issuer: ProviderDetail,
        @JvmField
        var credentialSchema: CredentialSchema,
        @JvmField
        var process: Process
    ) {
        data class CredentialSchema(
            @JvmField
            var id: String,
            @JvmField
            var type: CredentialSchemaType.CREDENTIAL_SCHEMA_TYPE,
            @JvmField
            var value: String
        )

        data class Process(
            @JvmField
            var endpoints: List<String>,
            @JvmField
            var reqE2e: ReqE2e,
            @JvmField
            var issuerNonce: String
        )
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
        var gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return gson.toJson(this)
    }

    override fun toString(): String = toJson()

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): IssueProfile {
            var gson = GsonBuilder()
                .registerTypeAdapterFactory(IntEnumAdapterFactory())
                .registerTypeAdapterFactory(StringEnumAdapterFactory())
                .disableHtmlEscaping()
                .create()
            return gson.fromJson(json, IssueProfile::class.java)
        }
    }
}
