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

package org.omnione.did.sdk.wallet.walletservice

import android.content.Context
import org.omnione.did.sdk.core.common.SecureEncryptor
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.DataModels.CipherInfo
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.DigestUtils
import org.omnione.did.sdk.utility.Encodings.Base16
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.wallet.WalletApi
import org.omnione.did.sdk.wallet.walletservice.db.DBManager
import org.omnione.did.sdk.wallet.walletservice.db.Preference
import org.omnione.did.sdk.wallet.walletservice.exception.WalletErrorCode
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.wallet.walletservice.logger.WalletLogger
import java.util.Arrays

class LockManager(private val context: Context) {
    private val walletLogger: WalletLogger = WalletLogger.getInstance()

    @Throws(UtilityException::class, WalletCoreException::class)
    fun registerLock(passCode: String, isLock: Boolean): Boolean {
        return if (isLock) {
            passCode.toByteArray().let { pwd ->
                Preference.loadWalletId(context).toByteArray().let { walletIdBytes ->
                    DigestUtils.getDigest(walletIdBytes, DigestEnum.DIGEST_ENUM.SHA_384).let { walletId ->
                        CryptoUtils.generateNonce(32).let { cek ->
                            val dk_keySize = 32
                            val iterator = 2048
                            val salt = Arrays.copyOfRange(walletId, 0, 32)

                            CryptoUtils.pbkdf2(pwd, salt, iterator, dk_keySize).let { kek ->
                                val key = Arrays.copyOfRange(kek, 0, 32)
                                val iv = Arrays.copyOfRange(walletId, 32, walletId.size)

                                CryptoUtils.encrypt(
                                    cek,
                                    CipherInfo(
                                        CipherInfo.ENCRYPTION_TYPE.AES,
                                        CipherInfo.ENCRYPTION_MODE.CBC,
                                        CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                                        CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                                    ),
                                    key,
                                    iv
                                ).let { encCek ->
                                    SecureEncryptor.encrypt(encCek, context).let { finalEncCek ->
                                        WalletLogger.d("cek : ${Base16.toHex(cek)} / ${cek.size}")
                                        updateFinalEncCek(Base16.toHex(finalEncCek))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            WalletApi.isLock = false
            true
        } else {
            updateFinalEncCek("")
            WalletApi.isLock = false
            true
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun authenticateLock(passCode: String) {
        passCode.toByteArray().let { pwd ->
            Preference.loadWalletId(context).toByteArray().let { walletIdBytes ->
                DigestUtils.getDigest(walletIdBytes, DigestEnum.DIGEST_ENUM.SHA_384).let { walletId ->
                    getFinalEncCek().let { finalEncCekStr ->
                        Base16.toBytes(finalEncCekStr).let { finalEncCek ->
                            SecureEncryptor.decrypt(finalEncCek).let { encCek ->
                                val dk_keySize = 32
                                val iterator = 2048
                                val salt = Arrays.copyOfRange(walletId, 0, 32)

                                CryptoUtils.pbkdf2(pwd, salt, iterator, dk_keySize).let { kek ->
                                    val key = Arrays.copyOfRange(kek, 0, 32)
                                    val iv = Arrays.copyOfRange(walletId, 32, walletId.size)

                                    CryptoUtils.decrypt(
                                        encCek,
                                        CipherInfo(
                                            CipherInfo.ENCRYPTION_TYPE.AES,
                                            CipherInfo.ENCRYPTION_MODE.CBC,
                                            CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                                            CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                                        ),
                                        key,
                                        iv
                                    ).also { cek ->
                                        WalletLogger.d("dec cek : ${Base16.toHex(cek)} / ${cek.size}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // unlock status
        WalletApi.isLock = false
    }

    fun isLock(): Boolean {
        return getFinalEncCek().let { finalEncCek ->
            if (finalEncCek.isEmpty()) {
                WalletLogger.d("wallet type is unlock")
                WalletApi.isLock = false
                false
            } else {
                WalletLogger.d("wallet type is lock")
                WalletApi.isLock = true
                true
            }
        }
    }

    private fun updateFinalEncCek(finalEncCek: String) {
        DBManager.getDatabases(context).let { walletDB ->
            walletDB.userDao()?.let { userDao ->
                userDao.getAll()[0].apply {
                    fek = finalEncCek
                }.let { user ->
                    userDao.update(user)
                }
            }
        }
    }

    private fun getFinalEncCek(): String {
        return DBManager.getDatabases(context).let { walletDB ->
            walletDB.userDao()?.let { userDao ->
                if (userDao.getAll().isNotEmpty()) {
                    userDao.getAll()[0].fek ?: ""
                } else {
                    ""
                }
            } ?: ""
        }
    }

    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    fun changeLock(oldPassCode: String, newPassCode: String) {
        // lock type setting
        getFinalEncCek().takeIf { it.isNotEmpty() }?.let { _ ->
            if (oldPassCode == newPassCode) {
                throw WalletException(WalletErrorCode.ERR_CODE_WALLET_NEW_PIN_EQUALS_OLD_PIN)
            }

            oldPassCode.toByteArray().let { pwd ->
                Preference.loadWalletId(context).toByteArray().let { walletIdBytes ->
                    DigestUtils.getDigest(walletIdBytes, DigestEnum.DIGEST_ENUM.SHA_384).let { walletId ->
                        getFinalEncCek().let { finalEncCekStr ->
                            Base16.toBytes(finalEncCekStr).let { finalEncCek ->
                                SecureEncryptor.decrypt(finalEncCek).let { encCek ->
                                    val dk_keySize = 32
                                    val iterator = 2048
                                    val salt = Arrays.copyOfRange(walletId, 0, 32)

                                    CryptoUtils.pbkdf2(pwd, salt, iterator, dk_keySize).let { kek ->
                                        val key = Arrays.copyOfRange(kek, 0, 32)
                                        val iv = Arrays.copyOfRange(walletId, 32, walletId.size)

                                        CryptoUtils.decrypt(
                                            encCek,
                                            CipherInfo(
                                                CipherInfo.ENCRYPTION_TYPE.AES,
                                                CipherInfo.ENCRYPTION_MODE.CBC,
                                                CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                                                CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                                            ),
                                            key,
                                            iv
                                        ).let { cek ->
                                            newPassCode.toByteArray().let { newPwd ->
                                                CryptoUtils.pbkdf2(newPwd, salt, iterator, dk_keySize).let { newKek ->
                                                    val newKey = Arrays.copyOfRange(newKek, 0, 32)

                                                    CryptoUtils.encrypt(
                                                        cek,
                                                        CipherInfo(
                                                            CipherInfo.ENCRYPTION_TYPE.AES,
                                                            CipherInfo.ENCRYPTION_MODE.CBC,
                                                            CipherInfo.SYMMETRIC_KEY_SIZE.AES_256,
                                                            CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5
                                                        ),
                                                        newKey,
                                                        iv
                                                    ).let { newEncCek ->
                                                        SecureEncryptor.encrypt(newEncCek, context).let { newFinalEncCek ->
                                                            updateFinalEncCek(Base16.toHex(newFinalEncCek))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } ?: throw WalletException(WalletErrorCode.ERR_CODE_WALLET_NOT_LOCK_TYPE)

        WalletApi.isLock = false
    }
}