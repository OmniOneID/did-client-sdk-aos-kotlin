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

import org.omnione.did.sdk.datamodel.did.AttestedDidDoc
import org.omnione.did.sdk.datamodel.did.SignedDidDoc
import org.omnione.did.sdk.datamodel.security.ReqEcdh
import org.omnione.did.sdk.datamodel.token.ServerTokenSeed

data class P132RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var attestedDidDoc: AttestedDidDoc? = null,
    var reqEcdh: ReqEcdh? = null,
    var seed: ServerTokenSeed? = null,
    var signedDidDoc: SignedDidDoc? = null,
    var serverToken: String? = null,
    var iv: String? = null,
    var kycTxId: String? = null
) : BaseRequestVo(id, txId)

