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

package org.omnione.did.sdk.wallet.walletservice.config

object Config {
    const val API_GATEWAY_GET_DID_DOC: String = "/api-gateway/api/v1/did-doc?did="
    const val TAS_GET_ALLOWED_CA: String = "/list/api/v1/allowed-ca/list?wallet="
    const val WALLET_PKG_NAME: String = "org.omnione.did.sdk.wallet"
    const val DID_METHOD: String = "omn"
    const val DID_CONTROLLER: String = "did:omn:tas"
}
