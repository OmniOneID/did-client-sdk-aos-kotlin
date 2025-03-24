package org.omnione.did.sdk.communication

interface NetworkResponse {
    fun onSuccess(response: String?)
    fun onError(t: Throwable?)
}