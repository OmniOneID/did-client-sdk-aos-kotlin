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

package org.omnione.did.sdk.wallet

import android.content.Context
import androidx.fragment.app.Fragment
import org.omnione.did.sdk.core.bioprompthelper.BioPromptHelper
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType.VERIFY_AUTH_TYPE
import org.omnione.did.sdk.datamodel.common.enums.WalletTokenPurpose.WALLET_TOKEN_PURPOSE
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.did.SignedDidDoc
import org.omnione.did.sdk.datamodel.profile.IssueProfile
import org.omnione.did.sdk.datamodel.profile.ReqE2e
import org.omnione.did.sdk.datamodel.security.DIDAuth
import org.omnione.did.sdk.datamodel.token.SignedWalletInfo
import org.omnione.did.sdk.datamodel.token.WalletTokenData
import org.omnione.did.sdk.datamodel.token.WalletTokenSeed
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import org.omnione.did.sdk.datamodel.vc.issue.ReturnEncVP
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.wallet.walletservice.LockManager
import org.omnione.did.sdk.wallet.walletservice.WalletToken
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.wallet.walletservice.logger.WalletLogger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class WalletApi private constructor(private val context: Context) {
    private val lockManager: LockManager
    private val walletCore: WalletCore
    private val walletToken: WalletToken
    private val walletService: WalletService
    private val walletLogger: WalletLogger
    private var bioPromptInterface: BioPromptHelper.BioPromptInterface? = null

    init {
        lockManager = LockManager(context)
        walletCore = WalletCore(context)
        walletToken = WalletToken(context, walletCore)
        walletService = WalletService(context, walletCore)
        walletLogger = WalletLogger.getInstance()
    }

    interface BioPromptInterface {
        fun onSuccess(result: String)
        fun onFail(result: String)
    }

    /**
     * @param bioPromptInterface
     */
    fun setBioPromptListener(bioPromptInterface: BioPromptHelper.BioPromptInterface) {
        this.bioPromptInterface = bioPromptInterface
    }

    fun isExistWallet(): Boolean {
        return WalletLogger.d("isExistWallet").let {
            walletCore.isExistWallet()
        }
    }

    /**
     * Creates a wallet and performs necessary setup operations such as fetching CA (Certificate App) package information
     * and creating a device DID (Decentralized Identifier) document. This method handles all the required steps to
     * initialize a wallet on the device.
     *
     * @return boolean - Returns `true` if the wallet exists after creation, otherwise `false`.
     * @throws Exception - If an error occurs during the wallet creation process, including issues with fetching CA package information,
     *                     creating the device DID document, or any other wallet-related operation.
     */
    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class, ExecutionException::class, InterruptedException::class)
    fun createWallet(walletUrl: String, tasUrl: String): Boolean {
        return WalletLogger.d("createWallet : $walletUrl / $tasUrl").run {
            walletService.fetchCaInfo(tasUrl)
            walletService.createDeviceDocument(walletUrl, tasUrl)
            isExistWallet()
        }
    }

    @Throws(WalletCoreException::class)
    fun deleteWallet() {
        WalletLogger.d("deleteWallet").also {
            walletService.deleteWallet()
        }
    }

    /**
     * Creates a wallet token seed for the specified purpose, package name, and user ID.
     *
     * @param purpose The purpose of the wallet token, defined by the `WALLET_TOKEN_PURPOSE` enum.
     * @param pkgName The CA package name associated with the wallet token.
     * @param userId The user ID for which the wallet token seed is being created.
     * @return WalletTokenSeed - The created wallet token seed.
     * @throws Exception - If an error occurs during the token seed creation process,
     *                     including wallet core issues or utility operation failures.
     */
    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun createWalletTokenSeed(purpose: WALLET_TOKEN_PURPOSE, pkgName: String, userId: String): WalletTokenSeed {
        return WalletLogger.d("createWalletTokenSeed").let {
            walletToken.createWalletTokenSeed(purpose, pkgName, userId)
        }
    }

    /**
     * Creates a nonce (a unique number used once) for the provided wallet token data.
     *
     * @param walletTokenData The data for which the nonce is being created, represented by the `WalletTokenData` object.
     * @return String - The created nonce.
     * @throws Exception - If an error occurs during the nonce creation process,
     *                     including wallet interaction, utility operation failures, or wallet core issues.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun createNonceForWalletToken(apiGateWayUrl: String, walletTokenData: WalletTokenData): String {
        return WalletLogger.d("createNonceForWalletToken").let {
            walletToken.createNonceForWalletToken(apiGateWayUrl, walletTokenData)
        }
    }

    /**
     * Binds a user to the wallet using the provided wallet token.
     *
     * @param hWalletToken The wallet token to be used for binding the user, as a `String`.
     * @return boolean - Returns `true` if the user is successfully bound, otherwise `false`.
     * @throws Exception - If an error occurs during the wallet token verification or user binding process.
     */
    @Throws(WalletException::class)
    fun bindUser(hWalletToken: String): Boolean {
        return WalletLogger.d("bindUser: + $hWalletToken").run {
            walletToken.verifyWalletToken(
                hWalletToken, listOf(
                    WALLET_TOKEN_PURPOSE.PERSONALIZE_AND_CONFIGLOCK,
                    WALLET_TOKEN_PURPOSE.PERSONALIZE
                )
            )
            walletService.bindUser()
        }
    }

    /**
     * Unbinds a user from the wallet using the provided wallet token.
     *
     * @param hWalletToken The wallet token to be used for unbinding the user.
     * @return boolean - Returns `true` if the user is successfully unbound, otherwise `false`.
     * @throws Exception - If an error occurs during the wallet token verification or user unbinding process.
     */
    @Throws(WalletException::class)
    fun unbindUser(hWalletToken: String): Boolean {
        return WalletLogger.d("unbindUser: + $hWalletToken").run {
            walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.DEPERSONALIZE))
            walletService.unbindUser()
        }
    }

    /**
     * Registers a wallet lock type with the provided wallet token, passcode, and lock status.
     *
     * @param hWalletToken The wallet token to be used for lock registration.
     * @param passCode The passcode associated with the lock.
     * @param isLock `true` if registering a lock, `false` if unregistering a lock.
     * @return boolean - Returns `true` if the lock is successfully registered or unregistered, otherwise `false`.
     * @throws Exception - If an error occurs during the wallet token verification, lock registration, or any related operation.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun registerLock(hWalletToken: String, passCode: String, isLock: Boolean): Boolean {
        return WalletLogger.d("registerLock: + $hWalletToken").run {
            walletToken.verifyWalletToken(
                hWalletToken, listOf(
                    WALLET_TOKEN_PURPOSE.PERSONALIZE_AND_CONFIGLOCK,
                    WALLET_TOKEN_PURPOSE.CONFIGLOCK
                )
            )
            lockManager.registerLock(passCode, isLock)
        }
    }

    /**
     * Authenticates a lock using the provided passcode.
     *
     * @param passCode The passcode used to authenticate the lock.
     * @throws Exception - If an error occurs during the lock authentication process, including utility or core issues.
     */
    @Throws(UtilityException::class, WalletCoreException::class)
    fun authenticateLock(passCode: String) {
        lockManager.authenticateLock(passCode)
    }

    /**
     * Creates a DID (Decentralized Identifier) document for the holder using the provided wallet token.
     *
     * @param hWalletToken The wallet token to be used for creating the DID document.
     * @return DIDDocument - The created DID document.
     * @throws Exception - If an error occurs during the wallet token verification, DID document creation, or any related operation.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun createHolderDIDDoc(hWalletToken: String): DIDDocument {
        return WalletLogger.d("createHolderDIDDoc: + $hWalletToken").run {
            walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.CREATE_DID))
            walletService.createHolderDIDDoc()
        }
    }

    /**
     * Creates a signed DID document using the provided owner DID document.
     *
     * @param ownerDIDDoc The DID document of the owner.
     * @return SignedDidDoc - The created signed DID document.
     * @throws Exception - Any error that occurs during the creation of the signed DID document.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun createSignedDIDDoc(ownerDIDDoc: DIDDocument): SignedDidDoc {
        return walletService.createSignedDIDDoc(ownerDIDDoc)
    }

    /**
     * Retrieves a DID document based on the specified type.
     *
     * @param type The type of the DID document. (1: device key, 2: holder key)
     * @return DIDDocument - The requested DID document.
     * @throws Exception - Any error that occurs while retrieving the DID document.
     */
    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    fun getDIDDocument(type: Int): DIDDocument? {
        return walletCore.getDocument(type)
    }

    /**
     * Generates a key pair using the provided wallet token and passcode.
     *
     * @param hWalletToken The wallet token used for key pair generation.
     * @param passcode The passcode required for key pair generation.
     * @throws Exception - Any error that occurs during wallet token verification or key pair generation.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun generateKeyPair(hWalletToken: String?, passcode: String?) {
        WalletLogger.d("generateKeyPair: + $hWalletToken").run {
            walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.CREATE_DID))
            walletCore.generateKeyPair(passcode)
        }
    }

    /**
     * Checks if the system is in a locked state.
     *
     * @return boolean - `true` if the system is locked, otherwise `false`.
     */
    fun isLock(): Boolean {
        return lockManager.isLock()
    }

    /**
     * Retrieves signed wallet information.
     *
     * @return SignedWalletInfo - The requested signed wallet information.
     * @throws Exception - Any error that occurs while retrieving the signed wallet information.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun getSignedWalletInfo(): SignedWalletInfo {
        return walletService.getSignedWalletInfo()
    }

    /**
     * Requests user registration with the given wallet token, transaction ID, server token, and signed DID document.
     *
     * @param hWalletToken The wallet token used for user registration.
     * @param tasUrl The URL of the TAS
     * @param txId The transaction ID.
     * @param serverToken The server-issued token.
     * @param signedDIDDoc The signed DID document.
     * @return CompletableFuture<String> - A `CompletableFuture` representing the result of the user registration request.
     * @throws Exception - Any error that occurs during wallet token verification or user registration request.
     */
    @Throws(WalletException::class)
    fun requestRegisterUser(hWalletToken: String, tasUrl: String, txId: String, serverToken: String, signedDIDDoc: SignedDidDoc): CompletableFuture<String> {
        return WalletLogger.d("requestRegisterUser: + $hWalletToken").run {
            walletToken.verifyWalletToken(
                hWalletToken, listOf(
                    WALLET_TOKEN_PURPOSE.CREATE_DID,
                    WALLET_TOKEN_PURPOSE.CREATE_DID_AND_ISSUE_VC
                )
            )
            walletService.requestRegisterUser(tasUrl, txId, serverToken, signedDIDDoc)
        }
    }

    /**
     * Requests user restoration with the given wallet token, server token, signed DID authentication, and transaction ID.
     *
     * @param hWalletToken The wallet token used for user restoration.
     * @param tasUrl The URL of the TAS (Trusted Authority Service).
     * @param serverToken The server-issued token.
     * @param signedDIDAuth The signed DID authentication document.
     * @param txId The transaction ID.
     * @return CompletableFuture<String> - A `CompletableFuture` representing the result of the user restoration request.
     * @throws Exception - Any error that occurs during wallet token verification, user restoration request, or related processes.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun requestRestoreUser(hWalletToken: String, tasUrl: String, serverToken: String, signedDIDAuth: DIDAuth, txId: String): CompletableFuture<String> {
        return WalletLogger.d("requestRestoreUser: + $hWalletToken").run {
            walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.RESTORE_DID))
            walletService.requestRestoreUser(tasUrl, serverToken, signedDIDAuth, txId)
        }
    }

    /**
     * Requests user DID update with the given wallet token, server token, signed DID authentication, and transaction ID.
     *
     * @param hWalletToken The wallet token used for user DID update.
     * @param tasUrl The URL of the TAS (Trusted Authority Service).
     * @param serverToken The server-issued token.
     * @param signedDIDAuth The signed DID authentication document.
     * @param txId The transaction ID.
     * @return CompletableFuture<String> - A `CompletableFuture` representing the result of the user DID update request.
     * @throws Exception - Any error that occurs during wallet token verification, user DID update request, or related processes.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun requestUpdateUser(hWalletToken: String, tasUrl: String, serverToken: String, signedDIDAuth: DIDAuth, txId: String): CompletableFuture<String> {
        return WalletLogger.d("requestUpdateUser: + $hWalletToken").run {
            walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.UPDATE_DID))
            walletService.requestUpdateUser(tasUrl, serverToken, signedDIDAuth, txId)
        }
    }

    /**
     * Creates signed DID authentication using the provided authentication nonce and passcode.
     *
     * @param authNonce The authentication nonce.
     * @param passcode The passcode.
     * @return DIDAuth - The signed DID authentication object.
     * @throws Exception - Any error that occurs during the DID authentication process.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun getSignedDIDAuth(authNonce: String, passcode: String?): DIDAuth {
        return walletService.getSignedDIDAuth(authNonce, passcode)
    }

    /**
     * Requests to issue a Verifiable Credential (VC) using the provided wallet token, server token, reference ID, profile, signed DID authentication, and transaction ID.
     *
     * @param hWalletToken The wallet token used for VC issuance.
     * @param serverToken The server-issued token.
     * @param refId The reference ID.
     * @param profile The issuance profile.
     * @param signedDIDAuth The signed DID authentication object.
     * @param txId The transaction ID.
     * @return CompletableFuture<String> - A `CompletableFuture` representing the result of the VC issuance request.
     * @throws Exception - Any error that occurs during wallet token verification or VC issuance request.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun requestIssueVc(
        hWalletToken: String,
        tasUrl: String,
        apiGateWayUrl: String,
        serverToken: String,
        refId: String,
        profile: IssueProfile,
        signedDIDAuth: DIDAuth,
        txId: String
    ): CompletableFuture<String> {
        return walletToken.verifyWalletToken(
            hWalletToken, listOf(
                WALLET_TOKEN_PURPOSE.ISSUE_VC,
                WALLET_TOKEN_PURPOSE.CREATE_DID_AND_ISSUE_VC
            )
        ).run {
            walletService.requestIssueVc(tasUrl, apiGateWayUrl, serverToken, refId, profile, signedDIDAuth, txId)
        }
    }

    /**
     * Requests to revoke a Verifiable Credential (VC) using the provided wallet token, server token, transaction ID, VC ID, issuer nonce, and passcode.
     *
     * @param hWalletToken The wallet token used for VC revocation.
     * @param serverToken The server-issued token.
     * @param txId The transaction ID.
     * @param vcId The ID of the VC to be revoked.
     * @param issuerNonce The issuer nonce.
     * @param passcode The passcode.
     * @return CompletableFuture<String> - A `CompletableFuture` representing the result of the VC revocation request.
     * @throws Exception - Any error that occurs during wallet token verification or VC revocation request.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class, ExecutionException::class, InterruptedException::class)
    fun requestRevokeVc(
        hWalletToken: String,
        tasUrl: String,
        serverToken: String,
        txId: String,
        vcId: String,
        issuerNonce: String,
        passcode: String,
        authType: VERIFY_AUTH_TYPE
    ): CompletableFuture<String> {
        return walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.REMOVE_VC)).run {
            walletService.requestRevokeVc(tasUrl, serverToken, txId, vcId, issuerNonce, passcode, authType)
        }
    }

    /**
     * Retrieves all Verifiable Credentials (VCs) associated with the provided wallet token.
     *
     * @param hWalletToken The wallet token used to retrieve the VC list.
     * @return List<VerifiableCredential> - A list of all VCs.
     * @throws Exception - Any error that occurs during wallet token verification or VC retrieval.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun getAllCredentials(hWalletToken: String): List<VerifiableCredential>? {
        return walletToken.verifyWalletToken(
            hWalletToken, listOf(
                WALLET_TOKEN_PURPOSE.LIST_VC,
                WALLET_TOKEN_PURPOSE.LIST_VC_AND_PRESENT_VP
            )
        ).run {
            walletCore.getAllCredentials()
        }
    }

    /**
     * Retrieves specific Verifiable Credentials (VCs) based on the provided identifiers.
     *
     * @param hWalletToken The wallet token used to retrieve the VCs.
     * @param identifiers A list of identifiers for the VCs to retrieve.
     * @return List<VerifiableCredential> - A list of requested VCs.
     * @throws Exception - Any error that occurs during wallet token verification or VC retrieval.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun getCredentials(hWalletToken: String, identifiers: List<String>): List<VerifiableCredential>? {
        return walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.DETAIL_VC)).run {
            walletCore.getCredentials(identifiers)
        }
    }

    /**
     * Deletes a Verifiable Credential (VC) using the provided wallet token and VC ID.
     *
     * @param hWalletToken The wallet token used for VC deletion.
     * @param vcId The ID of the VC to be deleted.
     * @throws Exception - Any error that occurs during wallet token verification or VC deletion.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun deleteCredentials(hWalletToken: String, vcId: String) {
        walletToken.verifyWalletToken(hWalletToken, listOf(WALLET_TOKEN_PURPOSE.REMOVE_VC)).run {
            walletCore.deleteCredentials(listOf(vcId))
        }
    }

    /**
     * Creates an encrypted Verifiable Presentation (VP) using the provided wallet token, VC ID, claim codes, end-to-end request object, passcode, nonce, and authentication type.
     *
     * @param hWalletToken The wallet token used for VP creation.
     * @param vcId The ID of the VC.
     * @param claimCode A list of claim codes to be included in the VP.
     * @param reqE2e The end-to-end request object.
     * @param passcode The passcode used for VP creation.
     * @param nonce The nonce used for VP creation.
     * @param authType The authentication type.
     * @return ReturnEncVP - The created encrypted VP object.
     * @throws Exception - Any error that occurs during wallet token verification or VP creation.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun createEncVp(
        hWalletToken: String,
        vcId: String,
        claimCode: List<String>,
        reqE2e: ReqE2e,
        passcode: String,
        nonce: String,
        authType: VERIFY_AUTH_TYPE
    ): ReturnEncVP {
        return walletToken.verifyWalletToken(
            hWalletToken, listOf(
                WALLET_TOKEN_PURPOSE.PRESENT_VP,
                WALLET_TOKEN_PURPOSE.LIST_VC_AND_PRESENT_VP
            )
        ).run {
            walletService.createEncVp(vcId, claimCode, reqE2e, passcode, nonce, authType)
        }
    }

    /**
     * Registers a biometric key for signing.
     *
     * @param ctx The context in which the biometric key will be registered.
     */
    @Throws(WalletException::class)
    fun registerBioKey(ctx: Context) {
        walletCore.apply {
            setBioPromptListener(object : BioPromptHelper.BioPromptInterface {
                override fun onSuccess(result: String) {
                    bioPromptInterface?.onSuccess(result)
                }

                override fun onError(result: String) {
                    // Type mismatch between BioPromptHelper.BioPromptInterface and WalletApi.BioPromptInterface
                    // This function exists in the helper interface but not in our defined interface
                }

                override fun onCancel(result: String) {
                    // Type mismatch between BioPromptHelper.BioPromptInterface and WalletApi.BioPromptInterface
                    // This function exists in the helper interface but not in our defined interface
                }

                override fun onFail(result: String) {
                    bioPromptInterface?.onFail(result)
                }
            })

            registerBioKey(ctx)
        }
    }

    /**
     * Authenticates a biometric key for signing.
     *
     * @param fragment The fragment used for biometric authentication.
     * @param ctx The context used for biometric authentication.
     * @throws Exception - Any error that occurs during biometric authentication.
     */
    @Throws(WalletCoreException::class, WalletException::class)
    fun authenticateBioKey(fragment: Fragment, ctx: Context) {
        walletCore.apply {
            setBioPromptListener(object : BioPromptHelper.BioPromptInterface {
                override fun onSuccess(result: String) {
                    bioPromptInterface?.onSuccess(result)
                }

                override fun onError(result: String) {
                    // Type mismatch between BioPromptHelper.BioPromptInterface and WalletApi.BioPromptInterface
                    // This function exists in the helper interface but not in our defined interface
                }

                override fun onCancel(result: String) {
                    // Type mismatch between BioPromptHelper.BioPromptInterface and WalletApi.BioPromptInterface
                    // This function exists in the helper interface but not in our defined interface
                }

                override fun onFail(result: String) {
                    bioPromptInterface?.onFail(result)
                }
            })

            authenticateBioKey(fragment, ctx)
        }
    }

    /**
     * Adds proofs to a document using the provided document, key IDs, DID, type, passcode, and DID authentication status.
     *
     * @param document The document to which proofs will be added.
     * @param keyIds The list of key IDs for the proofs.
     * @param did The DID.
     * @param type The DID document type.
     * @param passcode The passcode.
     * @param isDIDAuth Indicates if DID authentication is required.
     * @return ProofContainer - The document with added proofs.
     * @throws Exception - Any error that occurs during proof addition to the document.
     */
    @Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
    fun addProofsToDocument(
        document: ProofContainer,
        keyIds: List<String>,
        did: String,
        type: Int,
        passcode: String?,
        isDIDAuth: Boolean
    ): ProofContainer {
        return walletService.addProofsToDocument(document, keyIds, did, type, passcode, isDIDAuth)
    }

    /**
     * Checks if a biometric key is saved.
     *
     * @return boolean - `true` if a biometric key is saved, otherwise `false`.
     * @throws Exception - Any error that occurs during the check for a saved biometric key.
     */
    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    fun isSavedKey(id: String): Boolean {
        return walletCore.isSavedKey(id)
    }

    /**
     * Changes the Signing PIN for a given ID.
     *
     * @param keyId The identifier of the key to be changed.
     * @param oldPin The current PIN.
     * @param newPin The new PIN.
     * @throws Exception Throws an exception if parameter validation fails or if an error occurs during encryption/decryption.
     */
    @Throws(UtilityException::class, WalletCoreException::class)
    fun changePin(keyId: String, oldPin: String, newPin: String) {
        walletCore.changePin(keyId, oldPin, newPin)
    }

    /**
     * Changes the Unlock PIN.
     *
     * @param oldPin The current PIN.
     * @param newPin The new PIN.
     * @throws Exception Throws an exception if parameter validation fails or if an error occurs during encryption/decryption.
     */
    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    fun changeLock(oldPin: String, newPin: String) {
        lockManager.changeLock(oldPin, newPin)
    }

    companion object {
        @JvmStatic
        private var instance: WalletApi? = null
        var isLock = true

        @JvmStatic
        @Throws(WalletCoreException::class)
        fun getInstance(context: Context): WalletApi? {
            return instance ?: WalletApi(context).also {
                instance = it
            }
        }
    }
}