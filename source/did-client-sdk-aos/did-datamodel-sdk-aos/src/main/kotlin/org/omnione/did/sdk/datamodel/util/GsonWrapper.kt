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

package org.omnione.did.sdk.datamodel.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken

class GsonWrapper {
	private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
	private val gsonPretty: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

	fun getGson(): Gson = gson

	fun getGsonPrettyPrinting(): Gson = gsonPretty

	@Throws(JsonSyntaxException::class)
	inline fun <reified T> fromJson(json: String): T {
		val type = object : TypeToken<T>() {}.type
		return getGson().fromJson(json, type)
	}

	fun toJson(src: Any): String = gson.toJson(src)
}
