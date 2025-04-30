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

import org.omnione.did.sdk.datamodel.util.StringEnum;

class ClaimFormat {
    enum class CLAIM_FORMAT(val value: String) : StringEnum {
        plain("plain"),
        html("html"),
        xml("xml"),
        csv("csv"),
        png("png"),
        jpg("jpg"),
        gif("gif"),
        txt("txt"),
        pdf("pdf"),
        word("word");

        override fun getStringValue(): String {
            return value
        }

        override fun toString(): String = when (this) {
            plain -> "plain"
            html -> "html"
            xml -> "xml"
            csv -> "csv"
            png -> "png"
            jpg -> "jpg"
            gif -> "gif"
            txt -> "txt"
            pdf -> "pdf"
            word -> "word"
        }

        companion object {
            @JvmStatic
            fun fromValue(value: String): CLAIM_FORMAT? = 
                values().find { it.value == value }
        }
    }
}            