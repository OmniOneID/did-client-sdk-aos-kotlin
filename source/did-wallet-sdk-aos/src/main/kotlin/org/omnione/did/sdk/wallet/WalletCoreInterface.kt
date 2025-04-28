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
import org.omnione.did.sdk.core.exception.WalletCoreException
import org.omnione.did.sdk.core.vcmanager.datamodel.ClaimInfo
import org.omnione.did.sdk.core.vcmanager.datamodel.PresentationInfo
import org.omnione.did.sdk.datamodel.did.DIDDocument
import org.omnione.did.sdk.datamodel.vc.VerifiableCredential
import org.omnione.did.sdk.datamodel.vp.VerifiablePresentation
import org.omnione.did.sdk.utility.Errors.UtilityException
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException

interface WalletCoreInterface {
    @Throws(WalletCoreException::class, UtilityException::class)
    fun createDeviceDIDDoc(): DIDDocument

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun createHolderDIDDoc(): DIDDocument

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun generateKeyPair(passcode: String?)

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun getDocument(type: Int): DIDDocument?

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun saveDocument(type: Int)

    fun isExistWallet(): Boolean

    @Throws(WalletCoreException::class)
    fun deleteWallet()

    @Throws(WalletException::class)
    fun isAnyCredentialsSaved(): Boolean

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun addCredentials(verifiableCredential: VerifiableCredential)

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun getCredentials(identifiers: List<String>): List<VerifiableCredential>?

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun getAllCredentials(): List<VerifiableCredential>?

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun deleteCredentials(identifiers: List<String>)

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun makePresentation(claimInfos: List<ClaimInfo>, presentationInfo: PresentationInfo): VerifiablePresentation

    @Throws(WalletException::class)
    fun registerBioKey(ctx: Context)

    @Throws(WalletCoreException::class, WalletException::class)
    fun authenticateBioKey(fragment: Fragment, ctx: Context)

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun sign(id: String, pin: ByteArray, digest: ByteArray, type: Int): ByteArray?

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun verify(publicKey: ByteArray, digest: ByteArray, signature: ByteArray): Boolean

    @Throws(WalletCoreException::class, UtilityException::class, WalletException::class)
    fun isSavedKey(id: String): Boolean

    @Throws(WalletCoreException::class, UtilityException::class)
    fun changePin(keyId: String, oldPin: String, newPin: String)
}
