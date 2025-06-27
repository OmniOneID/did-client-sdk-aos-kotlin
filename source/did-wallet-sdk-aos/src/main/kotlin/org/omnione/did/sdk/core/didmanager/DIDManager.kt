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

package org.omnione.did.sdk.core.didmanager

import android.content.Context
import org.omnione.did.sdk.core.didmanager.datamodel.DIDKeyInfo
import org.omnione.did.sdk.core.didmanager.datamodel.DIDMeta
import org.omnione.did.sdk.core.didmanager.datamodel.DIDMethodType
import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.storagemanager.StorageManager
import org.omnione.did.sdk.core.storagemanager.datamodel.FileExtension
import org.omnione.did.sdk.core.storagemanager.datamodel.UsableInnerWalletItem
import org.omnione.did.sdk.datamodel.common.BaseObject
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.did.DIDKeyType
import org.omnione.did.sdk.datamodel.did.Service
import org.omnione.did.sdk.datamodel.did.VerificationMethod
import org.omnione.did.sdk.utility.CryptoUtils
import org.omnione.did.sdk.utility.Encodings.Base58
import org.omnione.did.sdk.utility.Errors.UtilityException
import java.text.SimpleDateFormat
import java.util.*

class DIDManager<E : BaseObject> {
    private var storageManager: StorageManager<DIDMeta, DIDDocument>? = null
    private var didDocument: DIDDocument? = null

    constructor()

    constructor(fileName: String, context: Context) {
        this.storageManager = StorageManager(
            fileName,
            FileExtension.FILE_EXTENSION.DID,
            true,
            context,
            DIDDocument::class.java,
            DIDMeta::class.java
        )
    }

    companion object {
        @JvmStatic
        @Throws(WalletCoreException::class, UtilityException::class)
        fun genDID(methodName: String): String {
            if (methodName.isEmpty() || methodName.length > 20 || !methodName.matches("^[a-zA-Z]*$".toRegex())) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "methodName")
            }
            val da = Base58.encode(CryptoUtils.generateNonce(20))
            return "did:$methodName:$da"
        }
    }

    @Throws(WalletCoreException::class)
    fun createDocument(did: String, keyInfos: List<DIDKeyInfo>, controller: String, service: List<Service>?) {
        if (isSaved()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_DOCUMENT_IS_ALREADY_EXISTS)
        }
        if (keyInfos.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "keyInfos")
        }
        if (keyInfos.size != keyInfos.toSet().size) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_DUPLICATED_PARAMETER, "keyInfos")
        }

        didDocument = DIDDocument(did, controller, getDate())

        val verificationMethod = mutableListOf<VerificationMethod>()
        for (keyInfo in keyInfos) {
            val method = VerificationMethod().apply {
                id = keyInfo.keyInfo.id
                type = DIDKeyType.DID_KEY_TYPE.secp256r1VerificationKey2018
                this.controller = controller
                publicKeyMultibase = keyInfo.keyInfo.publicKey
                authType = keyInfo.keyInfo.authType
            }
            verificationMethod.add(method)
        }
        didDocument?.verificationMethod = verificationMethod

        addMethodType(keyInfos)
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun getDocument(): DIDDocument {
        if (didDocument == null) {
            if (!isSaved()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_UNEXPECTED_CONDITION)
            }
            didDocument = storageManager?.getAllItems()?.get(0)?.item
        }
        return didDocument ?: throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_UNEXPECTED_CONDITION)
    }

    fun replaceDocument(didDocument: DIDDocument, needUpdate: Boolean) {
        this.didDocument = didDocument
        if (needUpdate) {
            updateTime()
        }
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun saveDocument() {
        didDocument?.let { doc ->
            val item = UsableInnerWalletItem<DIDMeta, DIDDocument>().apply {
                item = doc
                meta = DIDMeta().apply { id = doc.id }
            }
            if (isSaved()) {
                storageManager?.updateItem(item)
            } else {
                storageManager?.addItem(item, true)
            }
            didDocument = null
        }
    }

    @Throws(WalletCoreException::class)
    fun deleteDocument() {
        storageManager?.removeAllItems()
        didDocument = null
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun addVerificationMethod(keyInfo: DIDKeyInfo) {
        prepareToEditDocument()
        if (keyInfo == null) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "keyInfo")
        }

        val controller = keyInfo.controller ?: keyInfo.keyInfo.id
        val id = keyInfo.keyInfo.id

        val verificationMethod = VerificationMethod().apply {
            this.id = keyInfo.keyInfo.id
            type = DIDKeyType.DID_KEY_TYPE.secp256r1VerificationKey2018
            authType = keyInfo.keyInfo.authType
            this.controller = controller
            publicKeyMultibase = keyInfo.keyInfo.publicKey
        }

        val verificationMethodList = didDocument?.verificationMethod?.toMutableList() ?: mutableListOf()
        if (verificationMethodList.any { it.id == id }) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_DUPLICATE_KEY_ID_EXISTS_IN_VERIFICATION_METHOD)
        }
        verificationMethodList.add(verificationMethod)
        didDocument?.verificationMethod = verificationMethodList

        addMethodType(listOf(keyInfo))
        updateTime()
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun removeVerificationMethod(keyId: String) {
        prepareToEditDocument()
        if (keyId.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "keyId")
        }
        if (!isRegisteredKey(keyId)) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_NOT_FOUND_KEY_ID_IN_VERIFICATION_METHOD)
        }

        val verificationMethodList = didDocument?.verificationMethod?.toMutableList() ?: mutableListOf()
        verificationMethodList.removeAll { it.id == keyId }
        didDocument?.verificationMethod = verificationMethodList
        updateTime()
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun addService(service: Service) {
        prepareToEditDocument()
        val newServices = didDocument?.service?.toMutableList() ?: mutableListOf()
        if (newServices.any { it.id == service.id }) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_DUPLICATE_SERVICE_ID_EXISTS_IN_SERVICE)
        }
        newServices.add(service)
        didDocument?.service = newServices
        updateTime()
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    fun removeService(serviceId: String) {
        prepareToEditDocument()
        if (serviceId.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "serviceId")
        }
        val services = didDocument?.service?.toMutableList() ?: mutableListOf()
        if (services.isEmpty()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_NOT_FOUND_SERVICE_ID_IN_SERVICE)
        }
        if (!services.removeAll { it.id == serviceId }) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_NOT_FOUND_SERVICE_ID_IN_SERVICE)
        }
        didDocument?.service = if (services.isEmpty()) null else services
        updateTime()
    }

    @Throws(WalletCoreException::class)
    fun resetChanges() {
        if (!isSaved()) {
            throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_DUPLICATE_KEY_ID_EXISTS_IN_VERIFICATION_METHOD)
        }
        didDocument = null
    }

    @Throws(WalletCoreException::class, UtilityException::class)
    private fun prepareToEditDocument() {
        if (didDocument == null) {
            if (!isSaved()) {
                throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_UNEXPECTED_CONDITION)
            }
            didDocument = storageManager?.getAllItems()?.get(0)?.item
        }
    }

    @Throws(WalletCoreException::class)
    private fun addMethodType(keyInfos: List<DIDKeyInfo>) {
        val assertionMethod = mutableListOf<String>()
        val authentication = mutableListOf<String>()
        val keyAgreement = mutableListOf<String>()
        val capabilityInvocation = mutableListOf<String>()
        val capabilityDelegation = mutableListOf<String>()

        for (keyInfo in keyInfos) {
            for (methodType in keyInfo.methodType) {
                when (methodType) {
                    DIDMethodType.DID_METHOD_TYPE.assertionMethod -> assertionMethod.add(keyInfo.keyInfo.id)
                    DIDMethodType.DID_METHOD_TYPE.authentication -> authentication.add(keyInfo.keyInfo.id)
                    DIDMethodType.DID_METHOD_TYPE.keyAgreement -> keyAgreement.add(keyInfo.keyInfo.id)
                    DIDMethodType.DID_METHOD_TYPE.capabilityInvocation -> capabilityInvocation.add(keyInfo.keyInfo.id)
                    DIDMethodType.DID_METHOD_TYPE.capabilityDelegation -> capabilityDelegation.add(keyInfo.keyInfo.id)
                    else -> throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_DID_MANAGER_INVALID_PARAMETER, "didMethodType")
                }
            }
        }

        didDocument?.apply {
            if (assertionMethod.isNotEmpty()) this.assertionMethod = assertionMethod
            if (authentication.isNotEmpty()) this.authentication = authentication
            if (keyAgreement.isNotEmpty()) this.keyAgreement = keyAgreement
            if (capabilityInvocation.isNotEmpty()) this.capabilityInvocation = capabilityInvocation
            if (capabilityDelegation.isNotEmpty()) this.capabilityDelegation = capabilityDelegation
        }
    }

    private fun isRegisteredKey(keyId: String): Boolean {
        return didDocument?.verificationMethod?.any { it.id == keyId } ?: false
    }

    private fun updateTime() {
        didDocument?.updated = getDate()
    }

    fun isSaved(): Boolean {
        return storageManager?.isSaved() ?: false
    }

    private fun getDate(): String {
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(today)
    }
}