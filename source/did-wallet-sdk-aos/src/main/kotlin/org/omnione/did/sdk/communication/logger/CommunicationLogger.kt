package org.omnione.did.sdk.communication.logger

import android.util.Log

class CommunicationLogger private constructor() {

    companion object {
        @Volatile
        private var instance: CommunicationLogger? = null

        @JvmStatic
        fun getInstance(): CommunicationLogger {
            return instance ?: synchronized(this) {
                instance ?: CommunicationLogger().also { instance = it }
            }
        }
    }

    private var isEnabled = false

    fun enable() {
        isEnabled = true
    }

    fun disable() {
        isEnabled = false
    }

    private fun lineOut(): String {
        val level = 4
        val traces = Thread.currentThread().stackTrace
        return "at " + traces[level] + " "
    }

    private fun buildLogMsg(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]
        return " [ ${ste.fileName} :: ${ste.methodName} ] $message"
    }

    fun d(logMsg: String) {
        if (isEnabled) Log.d("COMMUNICATION_LOG", buildLogMsg("$logMsg :: ${lineOut()}"))
    }

    fun e(logMsg: String) {
        if (isEnabled) Log.e("COMMUNICATION_LOG", buildLogMsg("$logMsg :: ${lineOut()}"))
    }

    fun v(logMsg: String) {
        if (isEnabled) Log.v("COMMUNICATION_LOG", buildLogMsg("$logMsg :: ${lineOut()}"))
    }

    fun i(logMsg: String) {
        if (isEnabled) Log.i("COMMUNICATION_LOG", buildLogMsg("$logMsg :: ${lineOut()}"))
    }
}
