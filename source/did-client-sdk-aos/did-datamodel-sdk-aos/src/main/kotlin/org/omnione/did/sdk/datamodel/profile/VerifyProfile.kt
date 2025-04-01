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
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.common.enums.CredentialSchemaType
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class VerifyProfile(
    @SerializedName("id")
    @Expose var id: String?,

    @SerializedName("type")
    @Expose var type: ProfileType.PROFILE_TYPE?,

    @SerializedName("title")
    @Expose var title: String?,

    @SerializedName("description")
    @Expose var description: String?,

    @SerializedName("logo")
    @Expose var logo: LogoImage?,

    @SerializedName("encoding")
    @Expose var encoding: String?,

    @SerializedName("language")
    @Expose var language: String?,

    @SerializedName("profile")
    @Expose var profile: Profile?,

    @SerializedName("proof")
    @Expose var _proof: Proof?,

    @SerializedName("proofs")
    @Expose var _proofs: List<Proof>?
) : ProofContainer {
    data class Profile(
        @JvmField
        var verifier: ProviderDetail?,
        @JvmField
        var filter: ProfileFilter?,
        @JvmField
        var process: Process?
    )

    data class ProfileFilter(
        var credentialSchemas: List<CredentialSchema>?
    )

    data class CredentialSchema(
        var id: String?,
        @JvmField
        var type: CredentialSchemaType.CREDENTIAL_SCHEMA_TYPE?,
        @JvmField
        var varue: String?,
        @JvmField
        var displayClaims: List<String>?,
        @JvmField
        var requiredClaims: List<String>?,
        @JvmField
        var allowedIssuers: List<String>?,
        var presentAll: Boolean
    )

    data class Process(
        @JvmField
        var endpoints: List<String>?,
        @JvmField
        var reqE2e: ReqE2e?,
        @JvmField
        var verifierNonce: String?,
        @JvmField
        var authType: VerifyAuthType.VERIFY_AUTH_TYPE?
    )
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
        fun fromJsonString(json: String): VerifyProfile {
            var gson = GsonBuilder()
                .registerTypeAdapterFactory(IntEnumAdapterFactory())
                .registerTypeAdapterFactory(StringEnumAdapterFactory())
                .disableHtmlEscaping()
                .create()
            return gson.fromJson(json, VerifyProfile::class.java)
        }
    }
}
