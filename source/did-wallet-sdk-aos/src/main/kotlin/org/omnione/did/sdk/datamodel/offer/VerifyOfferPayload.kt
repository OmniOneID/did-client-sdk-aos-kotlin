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

package org.omnione.did.sdk.datamodel.offer;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.omnione.did.sdk.datamodel.common.SortData;
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

data class VerifyOfferPayload(
    @SerializedName("offerId")
    @Expose
    var offerId: String? = null,

    @SerializedName("type")
    @Expose
    var type: OFFER_TYPE? = null,

    @SerializedName("mode")
    @Expose
    var mode: PRESENT_MODE? = null,

    @SerializedName("device")
    @Expose
    var device: String? = null,

    @SerializedName("service")
    @Expose
    var service: String? = null,

    @SerializedName("endpoints")
    @Expose
    var endpoints: List<String>? = null,

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null,

    @SerializedName("locked")
    @Expose
    var locked: Boolean? = null

) : SortData() {

    override fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()

        val json = gson.toJson(this)
        return JsonSortUtil.sortJsonString(gson, json)
    }

    companion object {
        @JvmStatic
        fun fromJsonString(json: String): VerifyOfferPayload {
            val gson = Gson()
            return gson.fromJson(json, VerifyOfferPayload::class.java)
        }
    }

    enum class OFFER_TYPE(val value: String) {
        IssueOffer("IssueOffer"),
        VerifyOffer("VerifyOffer");

        fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            IssueOffer -> "IssueOffer"
            VerifyOffer -> "VerifyOffer"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): OFFER_TYPE? =
                values().find { it.value == value }
        }
    }

    enum class PRESENT_MODE(val value: String) {
        Direct("direct mode"),
        Indirect("indirect mode"),
        Proxy("proxy");


        fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            Direct -> "direct mode"
            Indirect -> "indirect mode"
            Proxy -> "proxy"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): PRESENT_MODE? =
                values().find { it.value == value }
        }
    }
}
