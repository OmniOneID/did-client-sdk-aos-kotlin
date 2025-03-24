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

package org.omnione.did.sdk.datamodel.protocol

import org.omnione.did.sdk.datamodel.security.AccE2e
import org.omnione.did.sdk.datamodel.security.ReqEcdh

data class P310RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var offerId: String? = null,
    var reqEcdh: ReqEcdh? = null,
    var accE2e: AccE2e? = null,
    var encVp: String? = null
) : BaseRequestVo(id, txId)