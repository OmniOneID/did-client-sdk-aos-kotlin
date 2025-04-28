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
import org.omnione.did.sdk.datamodel.security.DIDAuth
import org.omnione.did.sdk.datamodel.security.ReqEcdh
import org.omnione.did.sdk.datamodel.token.ServerTokenSeed

data class P210RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var vcPlanId: String? = null,
    var issuer: String? = null,
    var offerId: String? = null,
    var reqEcdh: ReqEcdh? = null,
    var seed: ServerTokenSeed? = null,
    var serverToken: String? = null,
    var didAuth: DIDAuth? = null,
    var accE2e: AccE2e? = null,
    var encReqVc: String? = null,
    var vcId: String? = null
) : BaseRequestVo(id, txId) {
    fun getDIDAuth() : DIDAuth? {
        return didAuth;
    }
    fun setDIDAuth(didAuth: DIDAuth?) {
        this.didAuth = didAuth;
    }
}
