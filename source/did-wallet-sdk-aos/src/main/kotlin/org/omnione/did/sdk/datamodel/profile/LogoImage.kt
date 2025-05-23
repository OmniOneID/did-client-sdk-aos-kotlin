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

package org.omnione.did.sdk.datamodel.profile;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.omnione.did.sdk.datamodel.common.enums.ClaimType
import org.omnione.did.sdk.datamodel.util.StringEnum;

data class LogoImage(
    @SerializedName("format")
    @Expose
    var format: LOGO_IMAGE_TYPE? = null,

    @SerializedName("link")
    @Expose
    var link: String? = null,

    @SerializedName("value")
    @Expose
    var value: String? = null
) {
    enum class LOGO_IMAGE_TYPE(val value: String) : StringEnum {
        jpg("jpg"),
        png("png");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            jpg -> "jpg"
            png -> "png"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): LOGO_IMAGE_TYPE? =
                values().find { it.value == value }
        }
    }
}
