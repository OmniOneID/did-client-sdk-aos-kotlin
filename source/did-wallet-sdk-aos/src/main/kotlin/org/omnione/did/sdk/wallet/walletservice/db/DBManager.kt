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
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.Volatile

@Database(entities = [Token::class, CaPkg::class, User::class], version = 1, exportSchema = false)
abstract class DBManager : RoomDatabase() {
    abstract fun tokenDao(): TokenDao?
    abstract fun caPkgDao(): CaPkgDao?
    abstract fun userDao(): UserDao?

    companion object {
        @Volatile
        private var INSTANCE: DBManager? = null
        const val NUMBER_OF_THREADS: Int = 4
        val databaseWriteExecutor
                : ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @JvmStatic
        fun getDatabases(context: Context?): DBManager {
            return INSTANCE ?: synchronized(this) {
                context?.let { ctx ->
                    val instance = Room.databaseBuilder(
                        ctx.applicationContext,
                        DBManager::class.java,
                        "wallet_database"
                    ).build()
                    INSTANCE = instance
                    instance
                } ?: throw IllegalArgumentException("Context cannot be null for database initialization")
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}