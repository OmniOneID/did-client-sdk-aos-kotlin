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

package org.omnione.did.sdk.wallet.walletservice.network.protocol

import android.content.Context
import org.omnione.did.sdk.communication.exception.CommunicationException
import org.omnione.did.sdk.datamodel.protocol.P141RequestVo
import org.omnione.did.sdk.datamodel.security.DIDAuth
import org.omnione.did.sdk.wallet.walletservice.network.HttpUrlConnection
import org.omnione.did.sdk.wallet.walletservice.util.WalletUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class UpdateUser {
    private var context: Context? = null

    constructor()
    constructor(context: Context) {
        this.context = context
    }

    fun updateUser(
        tasUrl: String,
        txId: String,
        serverToken: String,
        didAuth: DIDAuth
    ): CompletableFuture<String> {
        val api5 = "/tas/api/v1/request-update-diddoc"
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(
                    tasUrl + api5,
                    "POST",
                    M141_RequestUpdateDidDocByWallet(txId, serverToken, didAuth)
                )
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }.thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { throw CompletionException(it) }
    }

    private fun M141_RequestUpdateDidDocByWallet(
        txId: String,
        serverToken: String,
        didAuth: DIDAuth
    ): String {
        val requestVo = P141RequestVo(WalletUtil.createMessageId(), txId).apply {
            this.serverToken = serverToken
            this.didAuth = didAuth
        }
        return requestVo.toJson()
    }
}