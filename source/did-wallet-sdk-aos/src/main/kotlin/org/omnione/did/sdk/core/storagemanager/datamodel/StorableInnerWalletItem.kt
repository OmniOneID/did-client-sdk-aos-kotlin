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

package org.omnione.did.sdk.core.storagemanager.datamodel

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

class StorableInnerWalletItem<M : Meta> {
    var meta: M? = null
    var item: String? = null

    fun <T> fromJson(json: String, clazz: Class<T>) {
        val gson = Gson()
        val typeToken = object : TypeToken<StorableInnerWalletItem<M>>() {}.type
        val obj: StorableInnerWalletItem<M> = gson.fromJson(json, typeToken)
        this.meta = obj.meta
        this.item = obj.item
    }

    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        val json = gson.toJson(this)
        return JsonSortUtil.sortJsonString(gson, json)
    }
}
