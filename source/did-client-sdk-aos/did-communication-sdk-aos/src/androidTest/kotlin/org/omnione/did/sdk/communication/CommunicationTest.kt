package org.omnione.did.sdk.communication

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.omnione.did.sdk.communication.urlconnection.HttpUrlConnectionTask

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CommunicationTest {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        @Test
        fun useAppContext() {
        // Context of the app under test.
        //get request
        val getUrl = "https://httpbin.org/get"
        val getMethod = "GET"
        val getRequest = ""
        val httpFunc = HttpUrlConnectionTask()

        Log.d("CommunicationTest", "GET response : " + httpFunc.makeHttpRequest(getUrl, getMethod, getRequest))

        //post request
        val postUrl = "https://httpbin.org/post"
        val postMethod = "POST"
        val postRequest = "\"{\"message\": \"Hello, World!\"}\""

        Log.d("CommunicationTest", "POST response : " + httpFunc.makeHttpRequest(postUrl, postMethod, postRequest))

    }
}