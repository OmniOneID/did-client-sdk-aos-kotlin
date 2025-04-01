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

package org.omnione.did.sdk.wallet.walletservice.network

import org.omnione.did.sdk.communication.exception.CommunicationException
import org.omnione.did.sdk.communication.urlconnection.HttpUrlConnectionTask

class HttpUrlConnection : NetworkManager {
    @Throws(CommunicationException::class)
    override fun send(url: String, method: String, request: String): String {
        val httpFunc = HttpUrlConnectionTask()
        return httpFunc.makeHttpRequest(url, method, request)
    }
}