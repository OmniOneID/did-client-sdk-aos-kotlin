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

import org.omnione.did.sdk.datamodel.did.SignedDidDoc
import org.omnione.did.sdk.datamodel.security.DIDAuth
import org.omnione.did.sdk.datamodel.token.SignedWalletInfo
import org.omnione.did.sdk.datamodel.vc.issue.ReturnEncVP
import org.omnione.did.sdk.datamodel.common.ProofContainer
import org.omnione.did.sdk.datamodel.common.enums.VerifyAuthType
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.profile.IssueProfile
import org.omnione.did.sdk.datamodel.profile.ReqE2e
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException
import org.omnione.did.sdk.core.exception.WalletCoreException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

interface WalletServiceInterface {
    @Throws(WalletException::class, InterruptedException::class, ExecutionException::class)
    fun fetchCaInfo(tasUrl: String)

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class, InterruptedException::class, ExecutionException::class)
    fun createDeviceDocument(walletUrl: String, tasUrl: String)

    fun bindUser(): Boolean
    fun unbindUser(): Boolean

    @Throws(WalletCoreException::class, WalletException::class)
    fun deleteWallet()

    @Throws(UtilityException::class, WalletCoreException::class, WalletException::class)
    fun createHolderDIDDoc(): DIDDocument

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    fun createSignedDIDDoc(ownerDIDDoc: DIDDocument): SignedDidDoc

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    fun getSignedWalletInfo(): SignedWalletInfo

    @Throws(ExecutionException::class)
    fun requestRegisterUser(tasUrl: String, txId: String, serverToken: String, signedDIDDoc: SignedDidDoc): CompletableFuture<String>

    @Throws(WalletException::class, InterruptedException::class, ExecutionException::class)
    fun requestRestoreUser(tasUrl: String, serverToken: String, signedDIDAuth: DIDAuth, txId: String): CompletableFuture<String>

    @Throws(WalletException::class, InterruptedException::class, ExecutionException::class)
    fun requestUpdateUser(tasUrl: String, serverToken: String, signedDIDAuth: DIDAuth, txId: String): CompletableFuture<String>

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    fun getSignedDIDAuth(authNonce: String, pin: String?): DIDAuth

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class, InterruptedException::class, ExecutionException::class)
    fun requestIssueVc(
        tasUrl: String,
        apiGateWayUrl: String,
        serverToken: String,
        refId: String,
        profile: IssueProfile,
        signedDIDAuth: DIDAuth,
        txId: String
    ): CompletableFuture<String>

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class, InterruptedException::class, ExecutionException::class)
    fun requestRevokeVc(
        tasUrl: String,
        serverToken: String,
        txId: String,
        vcId: String,
        issuerNonce: String,
        passcode: String,
        authType: VerifyAuthType.VERIFY_AUTH_TYPE
    ): CompletableFuture<String>

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    fun createEncVp(
        vcId: String,
        claimCode: List<String>,
        reqE2e: ReqE2e,
        passcode: String,
        nonce: String,
        authType: VerifyAuthType.VERIFY_AUTH_TYPE
    ): ReturnEncVP

    @Throws(WalletException::class, WalletCoreException::class, UtilityException::class)
    fun addProofsToDocument(
        document: ProofContainer,
        keyIds: List<String>,
        did: String,
        type: Int,
        passcode: String?,
        isDIDAuth: Boolean
    ): ProofContainer
}