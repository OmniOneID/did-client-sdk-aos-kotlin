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

package org.omnione.did.sdk.wallet.walletservice.logger

import android.util.Log

class WalletLogger private constructor() {
    companion object {
        private var instance: WalletLogger? = null

        @JvmStatic
        fun getInstance(): WalletLogger = instance ?: synchronized(this) {
            instance ?: WalletLogger().also { instance = it }
        }

        @JvmStatic
        private var isEnabled: Boolean = false

        @JvmStatic
        fun enable() {
            isEnabled = true
        }

        @JvmStatic
        fun disable() {
            isEnabled = false
        }

        @JvmStatic
        private fun lineOut(): String = Thread.currentThread().stackTrace.let { traces ->
            "at ${traces[4]} "
        }

        @JvmStatic
        private fun buildLogMsg(message: String): String = Thread.currentThread().stackTrace[4].let { ste ->
            ste.fileName?.let { fileName ->
                "[ ${fileName.replace(".java", "")} :: ${ste.methodName} ] $message"
            } ?: message
        }

        @JvmStatic
        fun d(logMsg: String): String? = if (isEnabled) {
            buildLogMsg("$logMsg :: ${lineOut()}").also { formattedMsg ->
                Log.d("WALLET_LOG", formattedMsg)
            }
            logMsg
        } else {
            logMsg
        }

        @JvmStatic
        fun e(logMsg: String): String? = if (isEnabled) {
            buildLogMsg("$logMsg :: ${lineOut()}").also { formattedMsg ->
                Log.e("WALLET_LOG", formattedMsg)
            }
            logMsg
        } else {
            logMsg
        }

        @JvmStatic
        fun v(logMsg: String): String? = if (isEnabled) {
            buildLogMsg("$logMsg :: ${lineOut()}").also { formattedMsg ->
                Log.v("WALLET_LOG", formattedMsg)
            }
            logMsg
        } else {
            logMsg
        }

        @JvmStatic
        fun i(logMsg: String): String? = if (isEnabled) {
            buildLogMsg("$logMsg :: ${lineOut()}").also { formattedMsg ->
                Log.i("WALLET_LOG", formattedMsg)
            }
            logMsg
        } else {
            logMsg
        }
    }
}