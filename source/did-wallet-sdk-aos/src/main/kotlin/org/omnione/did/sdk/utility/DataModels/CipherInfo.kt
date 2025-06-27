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

package org.omnione.did.sdk.utility.DataModels

import com.google.gson.Gson

data class CipherInfo(
    var type: ENCRYPTION_TYPE? = null,
    var mode: ENCRYPTION_MODE? = null,
    var size: SYMMETRIC_KEY_SIZE? = null,
    var padding: SYMMETRIC_PADDING_TYPE? = null
) {

    enum class ENCRYPTION_TYPE(val value: String) {
        AES("AES");

        companion object {
            @JvmStatic
            fun fromValue(value: String): ENCRYPTION_TYPE? {
                return entries.find { it.value == value }
            }
        }

        override fun toString(): String = value
    }

    enum class ENCRYPTION_MODE(val value: String) {
        CBC("CBC"),
        ECB("ECB");

        companion object {
            @JvmStatic
            fun fromValue(value: String): ENCRYPTION_MODE? {
                return entries.find { it.value == value }
            }
        }

        override fun toString(): String = value
    }

    enum class SYMMETRIC_KEY_SIZE(val value: String) {
        AES_128("128"),
        AES_256("256");

        companion object {
            @JvmStatic
            fun fromValue(value: String): SYMMETRIC_KEY_SIZE? {
                return entries.find { it.value == value }
            }
        }

        override fun toString(): String = value
    }

    enum class SYMMETRIC_PADDING_TYPE(val key: String, val value: String) {
        NOPAD("NOPAD", "NOPAD"),
        PKCS5("PKCS5", "PKCS5Padding");

        companion object {
            @JvmStatic
            fun fromKey(key: String): SYMMETRIC_PADDING_TYPE? {
                return entries.find { it.key == key }
            }

            @JvmStatic
            fun fromValue(value: String): SYMMETRIC_PADDING_TYPE? {
                return entries.find { it.value == value }
            }
        }

        override fun toString(): String = value
    }

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}
