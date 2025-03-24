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

object Constants {
    const val TITLE: String = "Mobile Driver's License"

    // DID Doc Type (1: device key, 2 : holder)
    const val DID_DOC_TYPE_DEVICE: Int = 1
    const val DID_DOC_TYPE_HOLDER: Int = 2

    // KEY ID
    const val KEY_ID_ASSERT: String = "assert"
    const val KEY_ID_AUTH: String = "auth"
    const val KEY_ID_KEY_AGREE: String = "keyagree"
    const val KEY_ID_PIN: String = "pin"
    const val KEY_ID_BIO: String = "bio"

    // wallet name
    const val WALLET_DEVICE: String = "device"
    const val WALLET_HOLDER: String = "holder"
}
