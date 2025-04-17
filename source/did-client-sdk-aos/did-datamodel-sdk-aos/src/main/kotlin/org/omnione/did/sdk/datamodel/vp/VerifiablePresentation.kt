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

package org.omnione.did.sdk.datamodel.vp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import java.util.*

data class VerifiablePresentation @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String> = listOf("https://www.w3.org/ns/credentials/v2"),

    @SerializedName("id")
    @Expose
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("type")
    @Expose
    var type: List<String> = listOf("VerifiablePresentation"),

    @SerializedName("holder")
    @Expose
    var holder: String? = null,

    @SerializedName("validFrom")
    @Expose
    var validFrom: String? = null, // UTC date time

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null, // UTC date time

    @SerializedName("verifierNonce")
    @Expose
    var verifierNonce: String? = null,

    @SerializedName("verifiableCredential")
    @Expose
    var verifiableCredential: List<VerifiableCredential>? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer {

    constructor(holder: String, validFrom: String, validUntil: String, verifierNonce: String, verifiableCredential: List<VerifiableCredential>) : this() {
        this.holder = holder
        this.validFrom = validFrom
        this.validUntil = validUntil
        this.verifierNonce = verifierNonce
        this.verifiableCredential = verifiableCredential
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

    override fun setProofs(proofs: List<Proof>?) {
        this._proofs = proofs
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
