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

 package org.omnione.did.sdk.core.vcmanager

 import android.content.Context
 import org.omnione.did.sdk.datamodel.common.BaseObject
 import org.omnione.did.sdk.datamodel.vc.Claim
 import org.omnione.did.sdk.datamodel.vc.CredentialSubject
 import org.omnione.did.sdk.datamodel.vc.VCProof
 import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
 import org.omnione.did.sdk.datamodel.vp.VerifiablePresentation
 import org.omnione.did.sdk.utility.Errors.UtilityException
 import org.omnione.did.sdk.core.exception.WalletCoreErrorCode
 import org.omnione.did.sdk.core.exception.WalletCoreException
 import org.omnione.did.sdk.core.storagemanager.StorageManager
 import org.omnione.did.sdk.core.storagemanager.datamodel.FileExtension
 import org.omnione.did.sdk.core.storagemanager.datamodel.UsableInnerWalletItem
 import org.omnione.did.sdk.core.vcmanager.datamodel.VcMeta
 import org.omnione.did.sdk.core.vcmanager.datamodel.ClaimInfo
 import org.omnione.did.sdk.core.vcmanager.datamodel.PresentationInfo
 import java.util.*
 
 class VCManager<E : BaseObject> {
     private var storageManager: StorageManager<VcMeta, VerifiableCredential>? = null
 
     constructor()
 
     constructor(fileName: String, context: Context) {
         storageManager = StorageManager(
             fileName,
             FileExtension.FILE_EXTENSION.VC,
             true,
             context,
             VerifiableCredential::class.java,
             VcMeta::class.java
         )
     }
 
     /**
      * Checks if any credentials are saved.
      *
      * @return true if any credentials are saved, false otherwise
      */
     fun isAnyCredentialsSaved(): Boolean {
         return storageManager?.isSaved() ?: false
     }
 
     /**
      * Adds a verifiable credential.
      *
      * @param verifiableCredential the credential to be added
      * @throws WalletCoreException if the credential is null, the credential ID already exists, or the credential has no claims
      * @throws UtilityException if an error occurs
      */
     @Throws(WalletCoreException::class, UtilityException::class)
     fun addCredentials(verifiableCredential: VerifiableCredential) {
         if (verifiableCredential == null) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_INVALID_PARAMETER, "VerifiableCredential")
         }
 
         val item = UsableInnerWalletItem<VcMeta, VerifiableCredential>().apply {
             item = verifiableCredential
             meta = VcMeta().apply {
                 id = verifiableCredential.id
                 issuerId = verifiableCredential.issuer.id
                 credentialSchemaId = verifiableCredential.credentialSchema.id
                 credentialSchemaType = verifiableCredential.credentialSchema.type.value
             }
         }
         storageManager?.addItem(item, !isAnyCredentialsSaved())
     }
 
     /**
      * Gets specific verifiable credentials by their IDs.
      *
      * @param identifiers the list of credential IDs to get
      * @return a list of saved verifiable credentials
      * @throws WalletCoreException if no credentials are saved or the credentials are not found
      * @throws UtilityException if an error occurs
      */
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getCredentials(identifiers: List<String>): List<VerifiableCredential> {
         if (identifiers.size != identifiers.toSet().size) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_DUPLICATED_PARAMETER, "identifiers")
         }
 
         val walletItems = storageManager?.getItems(identifiers)

         if (walletItems != null)
             return walletItems.map { it.item }
         return mutableListOf()
     }
 
     /**
      * Gets all saved verifiable credentials.
      *
      * @return a list of all saved verifiable credentials
      * @throws WalletCoreException if no credentials are saved or no credentials are found
      * @throws UtilityException if an error occurs
      */
     @Throws(WalletCoreException::class, UtilityException::class)
     fun getAllCredentials(): List<VerifiableCredential> {
         val walletItems = storageManager?.getAllItems()

         if (walletItems != null)
             return walletItems.map { it.item }
         return mutableListOf()
     }
 
     /**
      * Deletes specific verifiable credentials by their IDs.
      *
      * @param identifiers the list of credential IDs to delete
      * @throws WalletCoreException if the credential IDs list is null or empty, or if no credentials are saved
      * @throws UtilityException if an error occurs
      */
     @Throws(WalletCoreException::class, UtilityException::class)
     fun deleteCredentials(identifiers: List<String>) {
         if (identifiers.isEmpty()) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_INVALID_PARAMETER, "identifiers")
         }
         if (identifiers.size != identifiers.toSet().size) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_DUPLICATED_PARAMETER, "identifiers")
         }
 
         storageManager?.removeItems(identifiers)
         if (storageManager?.getAllMetas()?.isEmpty() == true) {
             storageManager?.removeAllItems()
         }
     }
 
     /**
      * Deletes all saved verifiable credentials.
      *
      * @throws WalletCoreException if no credentials are saved
      */
     @Throws(WalletCoreException::class)
     fun deleteAllCredentials() {
         storageManager?.removeAllItems()
     }
 
     /**
      * Creates a Verifiable Presentation (without proof).
      *
      * @param claimInfos the list of claim information to include
      * @param presentationInfo the presentation information
      * @return the created Verifiable Presentation
      * @throws WalletCoreException if the input parameters are null or empty, or if no credentials are saved
      * @throws UtilityException if an error occurs
      */
     @Throws(WalletCoreException::class, UtilityException::class)
     fun makePresentation(claimInfos: List<ClaimInfo>, presentationInfo: PresentationInfo): VerifiablePresentation {
         if (claimInfos.isEmpty()) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_INVALID_PARAMETER, "claimInfos")
         }
         if (presentationInfo == null || presentationInfo.validUntil.isEmpty()) {
             throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_INVALID_PARAMETER, "presentationInfo")
         }
 
         val claimInfoMap = LinkedHashMap<String, List<String>>()
         val credentialIds = claimInfos.map { it.credentialId }
 
         claimInfos.forEach { claimInfo ->
             claimInfoMap[claimInfo.credentialId] = claimInfo.claimCodes
         }
 
         val vcListByIds = getCredentials(credentialIds)
         val filteredByRequestInfo = mutableListOf<VerifiableCredential>()
 
         for (vcByIds in vcListByIds) {
             val tempClaims = mutableListOf<Claim>()
             val tempProofValueList = mutableListOf<String>()
 
             vcByIds.credentialSubject.claims.forEach { claim ->
                 claimInfoMap[vcByIds.id]?.forEach { code ->
                     if (code == claim.code) {
                         tempClaims.add(claim)
                         tempProofValueList.add(vcByIds.proof.proofValueList?.get(vcByIds.credentialSubject.claims.indexOf(claim)) ?: "")
                     }
                 }
             }
 
             if (tempClaims.isEmpty()) {
                 throw WalletCoreException(WalletCoreErrorCode.ERR_CODE_VC_MANAGER_NO_CLAIM_CODE_IN_CREDENTIAL_FOR_PRESENTATION)
             }
 
             val tempCredentialSubject = vcByIds.credentialSubject.copy(claims = tempClaims)
             val tempVcProof = vcByIds.proof.copy(proofValue = null, proofValueList = tempProofValueList)
             val tempVc = vcByIds.copy(credentialSubject = tempCredentialSubject, proof = tempVcProof)
 
             filteredByRequestInfo.add(tempVc)
         }
 
         return VerifiablePresentation(
             presentationInfo.holder,
             presentationInfo.validFrom,
             presentationInfo.validUntil,
             presentationInfo.verifierNonce,
             filteredByRequestInfo
         )
     }
 }