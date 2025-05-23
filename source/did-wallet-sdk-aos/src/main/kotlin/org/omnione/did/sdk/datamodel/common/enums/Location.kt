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

package org.omnione.did.sdk.datamodel.common.enums;

import com.google.gson.annotations.SerializedName

class Location {
    enum class LOCATION(val value: String) {
        @SerializedName("inline")
        INLINE("inline"),
        @SerializedName("remote")
        REMOTE("remote"),
        @SerializedName("attach")
        ATTACH("attach");

        fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            INLINE -> "inline"
            REMOTE -> "remote"
            ATTACH -> "attach"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): LOCATION? = 
                values().find { it.value == value }
        }
    }
}
