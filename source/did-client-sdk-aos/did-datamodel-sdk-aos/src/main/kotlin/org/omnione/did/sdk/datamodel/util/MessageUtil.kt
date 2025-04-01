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

package org.omnione.did.sdk.datamodel.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

class MessageUtil {
    companion object {
        @JvmStatic
        fun <T> deserializeFromJsonElement(jsonElement: JsonElement, clazz: Class<T>): T {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(IntEnumAdapterFactory())
                .registerTypeAdapterFactory(StringEnumAdapterFactory())
                .create()
            return gson.fromJson(jsonElement, clazz)
        }

        @JvmStatic
        fun <T> deserialize(jsonString: String, clazz: Class<T>): T {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(IntEnumAdapterFactory())
                .registerTypeAdapterFactory(StringEnumAdapterFactory())
                .create()
            return gson.fromJson(jsonString, clazz)
        }

        @JvmStatic
        fun <T> serialize(clazz: Class<T>): String {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(IntEnumAdapterFactory())
                .registerTypeAdapterFactory(StringEnumAdapterFactory())
                .create()
            return gson.toJson(clazz)
        }
    }
}
