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

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory
import org.omnione.did.sdk.datamodel.util.JsonSortUtil
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory

class ExternalWallet<T> {
    var isEncrypted: Boolean = true
    var data: String = ""
    var version: Int = 0
    var signature: String = ""

    constructor()

    constructor(isEncrypted: Boolean, data: String) {
        this.isEncrypted = isEncrypted
        this.data = data
    }

//    fun isEncrypted(): Boolean {
//        return isEncrypted
//    }

    fun fromJson(json: String) {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        val type = object : TypeToken<ExternalWallet<T>>() {}.type
        val obj: ExternalWallet<T> = gson.fromJson(json, type)

        isEncrypted = obj.isEncrypted
        data = obj.data
        signature = obj.signature
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
