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

package org.omnione.did.sdk.datamodel.security

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.common.enums.EllipticCurveType
import org.omnione.did.sdk.datamodel.common.enums.SymmetricCipherType
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class ReqEcdh(
    @SerializedName("client")
    @Expose
    var client: String? = null,

    @SerializedName("clientNonce")
    @Expose
    var clientNonce: String? = null,

    @SerializedName("curve")
    @Expose
    var curve: EllipticCurveType.ELLIPTIC_CURVE_TYPE? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("candidate")
    @Expose
    var candidate: Ciphers? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null, // Key agreement proof

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer {

    data class Ciphers(
        @SerializedName("ciphers")
        @Expose
        var ciphers: List<SymmetricCipherType.SYMMETRIC_CIPHER_TYPE>? = null
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
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return JsonSortUtil.sortJsonString(gson, gson.toJson(this))
    }
}
