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

package org.omnione.did.sdk.datamodel.vc.issue

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.profile.LogoImage
import org.omnione.did.sdk.datamodel.vc.CredentialSchema

data class VCPlan(
    @SerializedName("vcPlanId")
    @Expose
    var vcPlanId: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("ref")
    @Expose
    var ref: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("validFrom")
    @Expose
    var validFrom: String? = null,

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null,

    @SerializedName("credentialSchema")
    @Expose
    var credentialSchema: CredentialSchema? = null,

    @SerializedName("option")
    @Expose
    var option: Option? = null,

    @SerializedName("allowedIssuers")
    @Expose
    var allowedIssuers: List<String>? = null,

    @SerializedName("manager")
    @Expose
    var manager: String? = null
) {
    data class Option(
        var allowUserInit: Boolean = false,
        var allowIssuerInit: Boolean = false,
        var delegatedIssuance: Boolean = false
    )
}
