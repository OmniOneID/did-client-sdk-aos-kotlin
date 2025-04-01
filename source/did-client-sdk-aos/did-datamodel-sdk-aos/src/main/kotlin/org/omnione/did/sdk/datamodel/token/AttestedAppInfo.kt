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

package org.omnione.did.sdk.datamodel.token

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.omnione.did.sdk.datamodel.common.Proof
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil

data class AttestedAppInfo(
    var appId: String? = null,
    var provider: Provider? = null,
    var nonce: String? = null,
    var proof: Proof? = null
) {
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        val json = gson.toJson(this)
        return JsonSortUtil.sortJsonString(gson, json)
    }
}
