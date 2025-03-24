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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import org.omnione.did.sdk.wallet.walletservice.util.WalletUtil

@Entity(tableName = "tbl_user")
data class User(
    @PrimaryKey
    @NotNull
    val idx: String = WalletUtil.genId(),

    @ColumnInfo(name = "pii")
    var pii: String? = null,

    @ColumnInfo(name = "final_enc_key")
    var fek: String? = null,

    @ColumnInfo(name = "create_date")
    var createDate: String? = null,

    @ColumnInfo(name = "update_date")
    var updateDate: String? = null
) {
    constructor(pii: String) : this(
        idx = WalletUtil.genId(),
        pii = pii,
        createDate = WalletUtil.getDate()
    )
}
