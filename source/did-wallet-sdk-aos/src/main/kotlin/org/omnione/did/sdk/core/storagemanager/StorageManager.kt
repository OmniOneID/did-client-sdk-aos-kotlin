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

 package org.omnione.did.sdk.core.storagemanager

 import android.content.Context
 import android.util.Base64
 import com.google.gson.JsonParser
 import org.json.JSONArray
 import org.json.JSONException
 import org.omnione.did.sdk.core.common.KeystoreManager
 import org.omnione.did.sdk.core.common.SecureEncryptor
 import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
 import org.omnione.did.sdk.core.exception.WalletCoreException
 import org.omnione.did.sdk.core.storagemanager.datamodel.ExternalWallet
 import org.omnione.did.sdk.core.storagemanager.datamodel.FileExtension
 import org.omnione.did.sdk.core.storagemanager.datamodel.Meta
 import org.omnione.did.sdk.core.storagemanager.datamodel.StorableInnerWalletItem
 import org.omnione.did.sdk.core.storagemanager.datamodel.UsableInnerWalletItem
 import org.omnione.did.sdk.datamodel.common.BaseObject
 import org.omnione.did.sdk.datamodel.util.MessageUtil
 import org.omnione.did.sdk.utility.DataModels.DigestEnum
 import org.omnione.did.sdk.utility.DataModels.MultibaseType
 import org.omnione.did.sdk.utility.DigestUtils
 import org.omnione.did.sdk.utility.Errors.UtilityException
 import org.omnione.did.sdk.utility.MultibaseUtils
 import java.io.*
 import java.security.*
 import java.security.cert.CertificateException
 import java.security.spec.InvalidKeySpecException
 import java.security.spec.InvalidParameterSpecException
 import java.util.*
 import javax.crypto.*
 import javax.crypto.spec.IvParameterSpec
 import javax.crypto.spec.PBEKeySpec
 import javax.crypto.spec.SecretKeySpec
 
 class StorageManager<M : Meta, T : BaseObject>(
     private val fileName: String,
     private val fileExtension: FileExtension.FILE_EXTENSION,
     private val isEncrypted: Boolean,
     private val context: Context,
     private val targetClass: Class<T>,
     private val metaClass: Class<M>
 ) {
     private val tmp: ExternalWallet<T> = ExternalWallet()
     private val filePath: String = context.filesDir.path + "/opendid_omnione/$fileName.${fileExtension.value}"
     private val walletPath: String = "/opendid_omnione/"
     private val SIGNATURE_MANAGER_ALIAS_PREFIX: String = "opendid_wallet_signature_"
     private val STORAGE_MANAGER_ALIAS: String = "signatureKey"
 
     fun isSaved(): Boolean {
         return File(filePath).exists()
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun addItem(walletItem: UsableInnerWalletItem<M, T>, asFirst: Boolean) {
         try {
             if (walletItem.toJson().isEmpty()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_INVALID_PARAMETER, "walletItem")
             }
 
             val storableInnerWalletItem = StorableInnerWalletItem<M>()
             val itemList = mutableListOf<String>()
 
             if (isSaved()) {
                 val list = getAllItems()
                 for (item in list) {
                     if (item.meta != null && walletItem.meta != null) {
                         if (item.meta.id == walletItem.meta.id) {
                             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_DUPLICATED_PARAMETER)
                         } else {
                             storableInnerWalletItem.meta = item.meta
                             storableInnerWalletItem.item = encodeItem(item)
                             itemList.add(storableInnerWalletItem.toJson())
                         }
                     }
                 }
             }
 
             if (asFirst) {
                 val deque = ArrayDeque(itemList)
                 storableInnerWalletItem.meta = walletItem.meta
                 storableInnerWalletItem.item = encodeItem(walletItem)
                 deque.addFirst(storableInnerWalletItem.toJson())
                 tmp.data = deque.toString()
             } else {
                 storableInnerWalletItem.meta = walletItem.meta
                 storableInnerWalletItem.item = encodeItem(walletItem)
                 itemList.add(storableInnerWalletItem.toJson())
                 tmp.data = itemList.toString()
             }
 
             writeFile()
         } catch (e: InvalidKeySpecException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: InvalidAlgorithmParameterException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: UnrecoverableEntryException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: NoSuchPaddingException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: CertificateException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: KeyStoreException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: NoSuchAlgorithmException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: InvalidKeyException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: IllegalBlockSizeException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: InvalidParameterSpecException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: BadPaddingException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         } catch (e: NoSuchProviderException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_SAVE_WALLET)
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun updateItem(walletItem: UsableInnerWalletItem<M, T>) {
         if (walletItem.toJson().isEmpty()) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_INVALID_PARAMETER, "walletItem")
         }
 
         val identifiers = mutableListOf<String>()
         val itemList = getAllItems()
 
         for (item in itemList) {
             if (item.meta != null && walletItem.meta != null) {
                 if (item.meta.id == walletItem.meta.id) {
                     identifiers.add(item.meta.id)
                     removeItems(identifiers)
                     addItem(walletItem, false)
                     return
                 }
             }
         }
 
         throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEM_TO_UPDATE)
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun removeItems(identifiers: List<String>) {
         try {
             if (identifiers.isEmpty()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_INVALID_PARAMETER, "identifiers")
             }
 
             val walletItems = getAllItems()
             val removeItems = walletItems.toMutableList()
             var removeItem: UsableInnerWalletItem<M, T>? = null
             var isIdentifiers = false
 
             for (id in identifiers) {
                 for (item in walletItems) {
                     if (item.meta.id == id) {
                         removeItem = item
                         removeItems.remove(removeItem)
                         isIdentifiers = true
                     }
                 }
 
                 if (!isIdentifiers) {
                     throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
                 }
             }
 
             val storableInnerWalletItem = StorableInnerWalletItem<M>()
             val updatedItemList = mutableListOf<String>()
 
             for (item in removeItems) {
                 storableInnerWalletItem.meta = item.meta
                 storableInnerWalletItem.item = encodeItem(item)
                 updatedItemList.add(storableInnerWalletItem.toJson())
             }
 
             tmp.data = updatedItemList.toString()
             writeFile()
         } catch (e: InvalidKeySpecException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: InvalidAlgorithmParameterException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: UnrecoverableEntryException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: NoSuchPaddingException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: CertificateException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: KeyStoreException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: IOException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: NoSuchAlgorithmException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: InvalidKeyException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: IllegalBlockSizeException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: InvalidParameterSpecException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: BadPaddingException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         } catch (e: NoSuchProviderException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_REMOVE)
         }
     }

     @Throws(WalletCoreException::class)
     fun removeAllItems() {
         try {
             if (isSaved()) {
                 val file = File(filePath)
                 if (file.exists()) {
                     file.delete()
                 } else {
                     throw FileNotFoundException("File does not exist: $filePath")
                 }
             }
         } catch (e: FileNotFoundException) {
             throw WalletCoreException(
                 WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_REMOVE_ITEMS,
                 e
             )
         } catch (e: SecurityException) {
             throw WalletCoreException(
                 WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAIL_TO_REMOVE_ITEMS,
                 e
             )
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getMetas(identifiers: List<String>): List<M> {
         try {
             val list = mutableListOf<M>()
             if (!isSaved()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_SAVED)
             }
 
             readFile()
             val parser = JsonParser()
             val element = parser.parse(tmp.toJson())
             val jsonObject = element.asJsonObject
 
             if (jsonObject.has("data")) {
                 verifyFile()
                 val array = JSONArray(tmp.data)
 
                 for (i in 0 until array.length()) {
                     val jsonElement = JsonParser.parseString(array.getString(i))
                     val metaObject = jsonElement.asJsonObject
 
                     for (id in identifiers) {
                         if (MessageUtil.deserializeFromJsonElement(metaObject.get("meta"), metaClass).id == id) {
                             list.add(MessageUtil.deserializeFromJsonElement(metaObject.get("meta"), metaClass))
                         }
                     }
                 }
             } else {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
             }
 
             return list
         } catch (e: JSONException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: IOException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: GeneralSecurityException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getAllMetas(): List<M> {
         try {
             val list = mutableListOf<M>()
             if (!isSaved()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_SAVED)
             }
 
             readFile()
             val parser = JsonParser()
             val element = parser.parse(tmp.toJson())
             val jsonObject = element.asJsonObject
 
             if (jsonObject.has("data")) {
                 verifyFile()
                 val array = JSONArray(tmp.data)
 
                 for (i in 0 until array.length()) {
                     val jsonElement = JsonParser.parseString(array.getString(i))
                     val metaObject = jsonElement.asJsonObject
                     list.add(MessageUtil.deserializeFromJsonElement(metaObject.get("meta"), metaClass))
                 }
             } else {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
             }
 
             return list
         } catch (e: JSONException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: IOException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: GeneralSecurityException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getItems(identifiers: List<String>): List<UsableInnerWalletItem<M, T>> {
         try {
             val list = mutableListOf<UsableInnerWalletItem<M, T>>()
             if (!isSaved()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_SAVED)
             }
 
             readFile()
             val parser = JsonParser()
             val element = parser.parse(tmp.toJson())
             val jsonObject = element.asJsonObject
 
             if (jsonObject.has("data")) {
                 verifyFile()
                 val array = JSONArray(tmp.data)
 
                 for (i in 0 until array.length()) {
                     val target = UsableInnerWalletItem<M, T>()
                     val jsonElement = JsonParser.parseString(array.getString(i))
                     val itemObject = jsonElement.asJsonObject
 
                     target.meta = MessageUtil.deserializeFromJsonElement(itemObject.get("meta"), metaClass)
                     val encodedItem = itemObject.get("item").asString
                     val decodeData = Base64.decode(encodedItem, Base64.DEFAULT)
                     val item = if (tmp.isEncrypted) {
                         String(SecureEncryptor.decrypt(decodeData))
                     } else {
                         String(decodeData)
                     }
 
                     for (id in identifiers) {
                         if (target.meta.id == id) {
                             target.item = MessageUtil.deserialize(item, targetClass)
                             list.add(target)
                         }
                     }
                 }
 
                 if (list.isEmpty()) {
                     throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
                 }
             } else {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
             }
 
             return list
         } catch (e: GeneralSecurityException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: JSONException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: IOException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getAllItems(): List<UsableInnerWalletItem<M, T>> {
         try {
             val list = mutableListOf<UsableInnerWalletItem<M, T>>()
             if (!isSaved()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_SAVED)
             }
 
             readFile()
             val parser = JsonParser()
             val element = parser.parse(tmp.toJson())
             val jsonObject = element.asJsonObject
 
             if (jsonObject.has("data")) {
                 verifyFile()
                 val array = JSONArray(tmp.data)
 
                 for (i in 0 until array.length()) {
                     val target = UsableInnerWalletItem<M, T>()
                     val jsonElement = JsonParser.parseString(array.getString(i))
                     val itemObject = jsonElement.asJsonObject
 
                     target.meta = MessageUtil.deserializeFromJsonElement(itemObject.get("meta"), metaClass)
                     val encodedItem = itemObject.get("item").asString
                     val decodeData = Base64.decode(encodedItem, Base64.DEFAULT)
                     val item = if (tmp.isEncrypted) {
                         String(SecureEncryptor.decrypt(decodeData))
                     } else {
                         String(decodeData)
                     }
 
                     target.item = MessageUtil.deserialize(item, targetClass)
                     list.add(target)
                 }
             } else {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
             }
 
             return list
         } catch (e: JSONException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: IOException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         } catch (e: GeneralSecurityException) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_NO_ITEMS_TO_FIND)
         }
     }
 
     @Throws(
         WalletCoreException::class,
         InvalidAlgorithmParameterException::class,
         UnrecoverableEntryException::class,
         NoSuchPaddingException::class,
         IllegalBlockSizeException::class,
         CertificateException::class,
         KeyStoreException::class,
         IOException::class,
         NoSuchAlgorithmException::class,
         InvalidParameterSpecException::class,
         BadPaddingException::class,
         NoSuchProviderException::class,
         InvalidKeyException::class
     )
     private fun encodeItem(item: UsableInnerWalletItem<M, T>): String {
         val itemJson = item.item.toJson()
         return if (isEncrypted) {
             val encData = SecureEncryptor.encrypt(itemJson.toByteArray(), context)
             Base64.encodeToString(encData, Base64.DEFAULT)
         } else {
             Base64.encodeToString(itemJson.toByteArray(), Base64.DEFAULT)
         }
     }
 
     @Throws(
         WalletCoreException::class,
         IOException::class,
         InvalidAlgorithmParameterException::class,
         CertificateException::class,
         KeyStoreException::class,
         NoSuchAlgorithmException::class,
         InvalidKeySpecException::class,
         NoSuchProviderException::class,
         UtilityException::class
     )
     private fun writeFile() {
         val directory = File(context.filesDir.path + walletPath)
         if (!directory.exists()) {
             directory.mkdirs()
         }
 
         val file = File(filePath)
         if (tmp.data != null) {
             tmp.isEncrypted = isEncrypted
             tmp.signature = getSignature()
         }
 
         FileWriter(file).use { fw ->
             fw.write(tmp.toJson())
         }
     }
 
     @Throws(WalletCoreException::class, IOException::class)
     private fun readFile() {
         val file = File(filePath)
         BufferedReader(FileReader(file)).use { br ->
             val buffer = StringBuilder()
             var line: String?
             while (br.readLine().also { line = it } != null) {
                 buffer.append(line)
             }
             tmp.fromJson(buffer.toString())
         }
     }
 
     @Throws(WalletCoreException::class, UtilityException::class)
     private fun getSignature(): String {
         if (!KeystoreManager.isKeySaved(SIGNATURE_MANAGER_ALIAS_PREFIX, STORAGE_MANAGER_ALIAS)) {
             KeystoreManager.generateKey(context, SIGNATURE_MANAGER_ALIAS_PREFIX, STORAGE_MANAGER_ALIAS)
         }
         val signature = KeystoreManager.sign(
             STORAGE_MANAGER_ALIAS,
             DigestUtils.getDigest(tmp.data.toByteArray(), DigestEnum.DIGEST_ENUM.SHA_256)
         )
         return MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, signature)
     }
 
     @Throws(WalletCoreException::class, GeneralSecurityException::class, IOException::class, UtilityException::class)
     private fun verifyFile() {
         val signature = MultibaseUtils.decode(tmp.signature)
             ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAILED_TO_DECODE, "externalWallet.signature")
 
         val digest = DigestUtils.getDigest(tmp.data.toByteArray(), DigestEnum.DIGEST_ENUM.SHA_256)
             ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_FAILED_TO_DECODE, "externalWallet.signature")
 
         val result = KeystoreManager.verify(
             KeystoreManager.getPublicKey(SIGNATURE_MANAGER_ALIAS_PREFIX, STORAGE_MANAGER_ALIAS),
             digest,
             signature
         )
 
         if (!result) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_STORAGE_MANAGER_MALFORMED_WALLET_SIGNATURE)
         }
     }
 }