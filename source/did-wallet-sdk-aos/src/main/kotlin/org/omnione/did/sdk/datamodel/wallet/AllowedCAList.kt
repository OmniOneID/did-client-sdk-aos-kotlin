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

package org.omnione.did.sdk.datamodel.wallet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AllowedCAList(
    @SerializedName("count")
    @Expose
    var count: Int = 0,

    @SerializedName("items")
    @Expose
    var items: List<String> = emptyList()
)
