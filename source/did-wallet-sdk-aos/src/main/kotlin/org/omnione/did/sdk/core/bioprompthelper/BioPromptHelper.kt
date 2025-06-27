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

package org.omnione.did.sdk.core.bioprompthelper

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class BioPromptHelper {
    private var context: Context? = null
    private var bioPromptInterface: BioPromptInterface? = null
    
    private companion object {
        private const val ERROR_FAILED = 7
        private const val ERROR_CANCELED_KEY = 10
        private const val ERROR_CANCELED_BUTTON = 13
    }
    
    constructor()
    
    constructor(context: Context) {
        this.context = context
    }
    
    fun setBioPromptListener(bioPromptInterface: BioPromptInterface) {
        this.bioPromptInterface = bioPromptInterface
    }
    
    interface BioPromptInterface {
        fun onSuccess(result: String)
        fun onFail(result: String)
        fun onError(result: String)
        fun onCancel(result: String)
    }
    
    /**
     * register a fingerprint for signing key
     * @param ctx Context for BiometricPrompt
     * @param message Message to display in the prompt
     */
    fun registerBioKey(ctx: Context, message: String?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val executor = ContextCompat.getMainExecutor(ctx)
            val biometricPrompt = BiometricPrompt(
                ctx as FragmentActivity,
                executor,
                createAuthCallback()
            )
            
            val promptInfo = createPromptInfo()
            
            biometricPrompt.authenticate(promptInfo)
        }
    }
    
    /**
     * authenticates a fingerprint for signing key
     * @param fragment Fragment for BiometricPrompt
     * @param ctx Context for BiometricPrompt
     * @param message Message to display in the prompt
     */
    fun authenticateBioKey(fragment: Fragment, ctx: Context, message: String?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val executor = ContextCompat.getMainExecutor(ctx)
            val biometricPrompt = BiometricPrompt(
                fragment,
                executor,
                createAuthCallback()
            )
            
            val promptInfo = createPromptInfo()
            
            biometricPrompt.authenticate(promptInfo)
        }
    }
    
    private fun createAuthCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    ERROR_FAILED -> bioPromptInterface?.onError("onAuthenticationError")
                    ERROR_CANCELED_BUTTON, ERROR_CANCELED_KEY -> bioPromptInterface?.onCancel("onAuthenticationCancel")
                    else -> bioPromptInterface?.onError("UnknownError")
                }
            }
            
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                bioPromptInterface?.onSuccess("onAuthenticationSucceeded")
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                bioPromptInterface?.onFail("onAuthenticationFailed")
            }
        }
    }
    
    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to access your private key")
            .setNegativeButtonText("Cancel")
            .build()
    }
}
