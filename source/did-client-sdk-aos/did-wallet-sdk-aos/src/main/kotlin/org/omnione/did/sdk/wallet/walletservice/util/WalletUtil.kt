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

package org.omnione.did.sdk.wallet.walletservice.util

import org.omnione.did.sdk.communication.exception.CommunicationException
import org.omnione.did.sdk.datamodel.common.enums.SymmetricCipherType
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.Encodings.Base16
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.wallet.walletservice.config.Config
import org.omnione.did.sdk.wallet.walletservice.network.HttpUrlConnection
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

object WalletUtil {
    @JvmStatic
    fun createMessageId(): String {
        val today = Date()
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmssSSSSSS")
        return dateFormat.format(today) + Base16.toHex(CryptoUtils.generateNonce(4))
    }

    @JvmStatic
    fun genId(): String {
        val id = UUID.randomUUID()
        return id.toString()
    }

    @JvmStatic
    fun getDate(): String {
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(today)
    }

    @JvmStatic
    fun createValidUntil(minute: Int): String {
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val cal = Calendar.getInstance()
        cal.time = today
        cal.add(Calendar.MINUTE, minute)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(cal.time)
    }

    @JvmStatic
    fun checkDate(targetDateStr: String): Boolean {
        val targetDate = ZonedDateTime.parse(targetDateStr)
        val today = ZonedDateTime.parse(Instant.now().toString())
        return today.isBefore(targetDate)
    }

    @JvmStatic
    fun getAllowedCa(tasUrl: String): CompletableFuture<String> {
        val api = Config.TAS_GET_ALLOWED_CA + Config.WALLET_PKG_NAME
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(tasUrl + api, "GET", "")
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }.thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    @JvmStatic
    fun getDidDoc(apiGateWayUrl: String, did: String): CompletableFuture<String> {
        val api = Config.API_GATEWAY_GET_DID_DOC + did
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(apiGateWayUrl + api, "GET", "")
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }.thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    @JvmStatic
    fun getCertVc(certVcUrl: String): CompletableFuture<String> {
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(certVcUrl, "GET", "")
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }.thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    @JvmStatic
    fun getVcSchema(schemaId: String): CompletableFuture<String> {
        val httpUrlConnection = HttpUrlConnection()

        return CompletableFuture.supplyAsync {
            runCatching {
                httpUrlConnection.send(schemaId, "GET", "")
            }.getOrElse { e ->
                when (e) {
                    is CommunicationException -> throw CompletionException(e)
                    else -> throw e
                }
            }
        }.thenCompose { CompletableFuture.completedFuture(it) }
            .exceptionally { ex ->
                throw CompletionException(ex)
            }
    }

    @JvmStatic
    @Throws(UtilityException::class)
    fun mergeSharedSecretAndNonce(
        sharedSecret: ByteArray,
        nonce: ByteArray,
        cipherType: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE
    ): ByteArray? {
        val length = sharedSecret.size + nonce.size
        val digest = ByteArray(length)
        System.arraycopy(sharedSecret, 0, digest, 0, sharedSecret.size)
        System.arraycopy(nonce, 0, digest, sharedSecret.size, nonce.size)

        val combinedResult = DigestUtils.getDigest(digest, DigestEnum.DIGEST_ENUM.SHA_256)

        return when (cipherType) {
            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES128CBC,
            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES128ECB -> {
                ByteArray(16).apply {
                    System.arraycopy(combinedResult, 0, this, 0, 16)
                }
            }
            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256CBC,
            SymmetricCipherType.SYMMETRIC_CIPHER_TYPE.AES256ECB -> {
                ByteArray(32).apply {
                    System.arraycopy(combinedResult, 0, this, 0, 32)
                }
            }
        }
    }
}