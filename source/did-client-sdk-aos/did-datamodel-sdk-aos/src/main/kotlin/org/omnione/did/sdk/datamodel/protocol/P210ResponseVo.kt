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

package org.omnione.did.sdk.datamodel.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.omnione.did.sdk.datamodel.profile.IssueProfile;
import org.omnione.did.sdk.datamodel.security.AccEcdh;
import org.omnione.did.sdk.datamodel.security.E2e;
import org.omnione.did.sdk.datamodel.util.IntEnumAdapterFactory;
import org.omnione.did.sdk.datamodel.util.StringEnumAdapterFactory;

data class P210ResponseVo @JvmOverloads constructor(
    override var txId: String? = null,
    override var code: Int? = null,
    override var message: String? = null,
    var refId: String? = null,
    var accEcdh: AccEcdh? = null,
    var iv: String? = null,
    var encStd: String? = null,
    var authNonce: String? = null,
    var profile: IssueProfile? = null,
    var e2e: E2e? = null
) : BaseResponseVo(txId, code, message) {

    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(IntEnumAdapterFactory())
            .registerTypeAdapterFactory(StringEnumAdapterFactory())
            .disableHtmlEscaping()
            .create()
        return gson.toJson(this)
    }
}
