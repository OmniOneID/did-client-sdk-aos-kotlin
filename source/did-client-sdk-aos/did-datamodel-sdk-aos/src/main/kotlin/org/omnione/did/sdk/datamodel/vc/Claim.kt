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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.omnione.did.sdk.datamodel.common.enums.ClaimFormat
import org.omnione.did.sdk.datamodel.common.enums.ClaimType
import org.omnione.did.sdk.datamodel.common.enums.Location

data class Claim(
    @SerializedName("code")
    @Expose
    var code: String? = null,

    @SerializedName("caption")
    @Expose
    var caption: String? = null,

    @SerializedName("value")
    @Expose
    var value: String? = null,

    @SerializedName("type")
    @Expose
    var type: ClaimType.CLAIM_TYPE? = null,

    @SerializedName("format")
    @Expose
    var format: ClaimFormat.CLAIM_FORMAT? = null,

    @SerializedName("hideValue")
    @Expose
    var hideValue: Boolean = false,

    @SerializedName("location")
    @Expose
    var location: Location.LOCATION? = null,

    @SerializedName("digestSRI")
    @Expose
    var digestSRI: String? = null,

    @SerializedName("i18n")
    @Expose
    var i18n: Map<String, Internationalization>? = null
) {
    data class Internationalization(
        var caption: String? = null,
        var value: String? = null,
        var digestSRI: String? = null
    )
}
