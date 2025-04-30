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

package org.omnione.did.sdk.wallet.walletservice.db

import android.content.Context
import android.content.SharedPreferences

object Preference {

    @JvmStatic
    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun saveWalletId(context: Context, walletId: String) {
        val prefs = getPreference(context)
        with(prefs.edit()) {
            putString("walletId", walletId)
            apply()
        }
    }

    @JvmStatic
    fun loadWalletId(context: Context): String {
        val prefs = getPreference(context)
        return prefs.getString("walletId", "") ?: ""
    }

    @JvmStatic
    fun savePii(context: Context, pii: String) {
        val prefs = getPreference(context)
        with(prefs.edit()) {
            putString("pii", pii)
            apply()
        }
    }

    @JvmStatic
    fun loadPii(context: Context): String {
        val prefs = getPreference(context)
        return prefs.getString("pii", "") ?: ""
    }
}
