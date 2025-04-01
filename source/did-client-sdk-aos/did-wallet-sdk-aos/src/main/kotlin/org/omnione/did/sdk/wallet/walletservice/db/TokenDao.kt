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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TokenDao {
    @Query("SELECT * FROM tbl_token")
    fun getAll(): List<Token>

    @Query("SELECT * FROM tbl_token WHERE idx IN (:id)")
    fun loadAllByIds(id: Array<String>): List<Token>

    @Insert
    fun insertAll(vararg tokens: Token)

    @Update
    fun update(token: Token)

    @Delete
    fun delete(token: Token)

    @Query("DELETE FROM tbl_token WHERE idx = :idx")
    fun deleteByIdx(idx: String): Int

    @Query("DELETE FROM tbl_token")
    fun deleteAll()
}
