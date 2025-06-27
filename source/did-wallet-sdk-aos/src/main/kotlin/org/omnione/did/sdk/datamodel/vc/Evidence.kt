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
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Evidence(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: EvidenceType.EVIDENCE_TYPE = EvidenceType.EVIDENCE_TYPE.documentVerification,

    @SerializedName("verifier")
    @Expose
    var verifier: String? = null,

    @SerializedName("evidenceDocument")
    @Expose
    var evidenceDocument: String? = null,

    @SerializedName("subjectPresence")
    @Expose
    var subjectPresence: Presence.PRESENCE? = null,

    @SerializedName("documentPresence")
    @Expose
    var documentPresence: Presence.PRESENCE? = null,

    @SerializedName("attribute")
    @Expose
    var attribute: HashMap<String, String>? = null
) {
    constructor(verifier: String, evidenceDocument: String, subjectPresence: Presence.PRESENCE, documentPresence: Presence.PRESENCE) : this() {
        this.verifier = verifier
        this.evidenceDocument = evidenceDocument
        this.subjectPresence = subjectPresence
        this.documentPresence = documentPresence
    }

    fun fromJson(json: String) {
        val gson = Gson()
        val obj = gson.fromJson(json, Evidence::class.java)
        this.id = obj.id
        this.verifier = obj.verifier
        this.evidenceDocument = obj.evidenceDocument
        this.subjectPresence = obj.subjectPresence
        this.documentPresence = obj.documentPresence
        this.attribute = obj.attribute
    }
}
