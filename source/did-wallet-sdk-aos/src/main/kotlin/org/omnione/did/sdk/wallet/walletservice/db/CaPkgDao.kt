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

@Dao
interface CaPkgDao {
    @Query("SELECT * FROM tbl_ca_pkg")
    fun getAll(): List<CaPkg>

    @Query("SELECT * FROM tbl_ca_pkg WHERE idx IN (:id)")
    fun loadAllByIds(id: IntArray): List<CaPkg>

    @Insert
    fun insertAll(vararg caPkgs: CaPkg)

    @Delete
    fun delete(caPkg: CaPkg)

    @Query("DELETE FROM tbl_ca_pkg")
    fun deleteAll()
}
