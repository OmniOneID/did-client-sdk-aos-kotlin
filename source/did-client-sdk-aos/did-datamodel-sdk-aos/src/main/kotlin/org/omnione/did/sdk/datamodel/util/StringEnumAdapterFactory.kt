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

import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class StringEnumAdapterFactory : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        // Only handle StringEnum enums
        if (!StringEnum::class.java.isAssignableFrom(rawType) || !Enum::class.java.isAssignableFrom(rawType)) {
            return null
        }

        return object : TypeAdapter<StringEnum>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: StringEnum) {
                //Log.d("test", "write : " + value + " / " + rawType);
                out.value(value.getStringValue())
            }

            @Throws(IOException::class)
            override fun read(`in`: JsonReader): StringEnum {
                val value = `in`.nextString()

                return runCatching {
                    val fromValueMethod: Method = rawType.getMethod("fromValue", String::class.java)
                    fromValueMethod.invoke(null, value) as StringEnum
                }.getOrElse { e ->
                    when (e) {
                        is NoSuchMethodException, is IllegalAccessException, is InvocationTargetException -> throw IOException("Failed to deserialize enum", e)
                        else -> throw e
                    }
                }
            }
        } as TypeAdapter<T>
    }
}