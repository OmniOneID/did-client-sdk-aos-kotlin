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
import com.google.gson.internal.LinkedTreeMap
import java.util.*

class JsonSortUtil {

	companion object {
		@JvmStatic
		private fun sortTreeMapChange(map: MutableMap<String, Any>): TreeMap<String, Any> {
			val resultTreeMap = TreeMap<String, Any>()

			for ((key, value) in map) {
				when (value) {
					is LinkedTreeMap<*, *> -> {
						@Suppress("UNCHECKED_CAST")
						val linkedTreeMap = value as MutableMap<String, Any>
						val treeMap = sortTreeMapChange(TreeMap(linkedTreeMap))
						resultTreeMap[key] = treeMap
					}
					is ArrayList<*> -> {
						val listDataTemp = ArrayList<Any>()
						for (obj in value) {
							listDataTemp.add(
								when (obj) {
									is String -> obj
									is Double -> obj.toInt()
									is LinkedTreeMap<*, *> -> {
										@Suppress("UNCHECKED_CAST")
										val newObject = obj as MutableMap<String, Any>
										sortTreeMapChange(newObject)
									}
									else -> obj ?: continue
								}
							)
						}
						resultTreeMap[key] = listDataTemp
					}
					is Double -> resultTreeMap[key] = value.toInt()
					else -> resultTreeMap[key] = value
				}
			}
			return resultTreeMap
		}
		@JvmStatic
		fun sortJsonString(gson: Gson, json: String): String {
			val map: MutableMap<String, Any> = gson.fromJson(json, MutableMap::class.java) as MutableMap<String, Any>
			val converterMap = sortTreeMapChange(TreeMap(map))
			return gson.toJson(converterMap)
		}
		@JvmStatic
		fun sortJsonString(gson: GsonWrapper, json: String): String {
			val map: MutableMap<String, Any> = gson.fromJson(json)
			val converterMap = TreeMap(map)
			return gson.toJson(converterMap)
		}
	}
}

