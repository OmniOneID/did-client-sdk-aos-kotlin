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

package org.omnione.did.sdk.core.keymanager.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class KeyGenWalletMethodType(
    @SerializedName("walletMethodType")
    @Expose
    var walletMethodType: WALLET_METHOD_TYPE = WALLET_METHOD_TYPE.NONE,

    @SerializedName("pin")
    @Expose
    var pin: String = ""
) {
    constructor(pin: String) : this(WALLET_METHOD_TYPE.PIN, pin)

    constructor(walletMethodType: WALLET_METHOD_TYPE) : this(walletMethodType, "")

    constructor() : this(WALLET_METHOD_TYPE.NONE, "")

    enum class WALLET_METHOD_TYPE(val value: String) {
        NONE("NONE"),
        PIN("PIN");

        companion object {
            @JvmStatic
            fun fromValue(value: String): WALLET_METHOD_TYPE? =
                values().find { it.value == value }
        }

        override fun toString(): String = value
    }
}

