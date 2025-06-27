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

package org.omnione.did.sdk.datamodel.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.omnione.did.sdk.datamodel.common.enums.ProofPurpose;
import org.omnione.did.sdk.datamodel.common.enums.ProofType;
import org.omnione.did.sdk.datamodel.did.DidDocVo
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory;
import org.omnione.did.sdk.datamodel.util.JsonSortUtil;
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory;

open class Proof @JvmOverloads constructor(
    var created: String? = null, // UTC date time
    var proofPurpose: ProofPurpose.PROOF_PURPOSE? = null,
    var verificationMethod: String? = null,
    var type: ProofType.PROOF_TYPE? = null,
    var proofValue: String? = null
) {
    open fun copy(
        created: String? = this.created,
        proofPurpose: ProofPurpose.PROOF_PURPOSE? = this.proofPurpose,
        verificationMethod: String? = this.verificationMethod,
        type: ProofType.PROOF_TYPE? = this.type,
        proofValue: String? = this.proofValue
    ) = Proof(created, proofPurpose, verificationMethod, type, proofValue)

    fun fromJson(json: String): Proof {
        val gson = Gson()
        return gson.fromJson(json, Proof::class.java)
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
        return "Proof(" +
                "created=$created, " +
                "proofPurpose=$proofPurpose, " +
                "verificationMethod=$verificationMethod, " +
                "type=$type, " +
                "proofValue=$proofValue)"
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): Proof {
            val gson = Gson()
            return gson.fromJson(json, Proof::class.java)
        }
    }
}
