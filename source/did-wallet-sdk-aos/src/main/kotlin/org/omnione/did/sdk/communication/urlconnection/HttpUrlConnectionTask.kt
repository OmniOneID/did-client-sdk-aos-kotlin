package org.omnione.did.sdk.communication.urlconnection

import org.omnione.did.sdk.communication.exception.CommunicationErrorCode
import org.omnione.did.sdk.communication.exception.CommunicationException
import org.omnione.did.sdk.communication.logger.CommunicationLogger
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class HttpUrlConnectionTask {

    private var communicationLogger: CommunicationLogger = CommunicationLogger.getInstance()

    @Throws(CommunicationException::class)
    fun makeHttpRequest(urlString: String, method: String, payload: String): String {
        communicationLogger.d("request : $payload / $urlString / [$method]")

        if (urlString.isEmpty()) {
            throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_INVALID_PARAMETER, "urlString")
        }
        if (method.isEmpty()) {
            throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_INVALID_PARAMETER, "method")
        }
        if (payload.isEmpty() && method.equals("POST", ignoreCase = true)) {
            throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_INVALID_PARAMETER, "payload")
        }

        var urlConnection: HttpURLConnection? = null

        return try {
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = method
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.setRequestProperty("Accept", "application/json")

            if (method.equals("POST", ignoreCase = true)) {
                urlConnection.doOutput = true
                urlConnection.outputStream.use { os ->
                    val input = payload.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
            }

            urlConnection.connect()
            val responseCode = urlConnection.responseCode

            when (responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    urlConnection.inputStream.bufferedReader().use { it.readText() }
                }
                HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_SERVER_ERROR -> {
                    val errorMsg = urlConnection.errorStream.bufferedReader().use { it.readText() }
                    throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_SERVER_FAIL, errorMsg)
                }
                else -> {
                    throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_INCORRECT_URL_CONNECTION, urlString)
                }
            }
        } catch (e: IOException) {
            throw CommunicationException(CommunicationErrorCode.ERR_CODE_COMMUNICATION_INCORRECT_URL_CONNECTION, e.message ?: "Unknown error")
        } finally {
            urlConnection?.disconnect()
        }
    }
}
