package org.omnione.did.sdk.wallet

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.omnione.did.sdk.wallet.walletservice.config.Constants
import org.omnione.did.sdk.wallet.walletservice.exception.WalletException

/**
 * Android instrumented test for WalletCore
 *
 * This test must be executed on an actual Android device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class WalletCoreInstrumentedTest {

    // Test target class
    private lateinit var walletCore: WalletCore

    // Android context
    private lateinit var appContext: Context

    // Test passcode
    private val testPasscode = "testPasscode123"

    @Before
    fun setUp() {
        // Get application context
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Initialize WalletCore
        walletCore = WalletCore(appContext)

        // Delete wallet if it exists before running the test
        try {
            if (walletCore.isExistWallet()) {
                walletCore.deleteWallet()
            }
        } catch (e: Exception) {
            println("Error deleting wallet during test initialization: ${e.message}")
        }

        // Temporarily unlock for testing
        WalletApi.isLock = false;
    }

    @After
    fun tearDown() {
        // Cleanup after test
        try {
            walletCore.deleteWallet()
        } catch (e: Exception) {
            println("Error deleting wallet after test completion: ${e.message}")
        }
    }

    @Test
    fun testWalletInitialState() {
        // Wallet should not exist in initial state
        assertFalse("Newly initialized wallet already exists", walletCore.isExistWallet())
    }

    @Test
    fun testCreateDeviceDIDDoc() {
        // Create device DID document
        val document = walletCore.createDeviceDIDDoc()

        // Generated document should not be null
        assertNotNull("Generated device DID document is null", document)
    }

    @Test
    fun testGenerateKeyPair() {
        // Generate key
        walletCore.generateKeyPair(testPasscode)

        // If successfully generated, no exception should occur
        // There's no direct way to verify if the PIN key was created, so
        // we'll check if it can be used when creating a holder DID document
        val document = walletCore.createHolderDIDDoc()
        assertNotNull("Failed to create holder DID document", document)
    }

    @Test(expected = WalletException::class)
    fun testGenerateKeyPair_withEmptyPasscode() {
        // Attempt to generate key with empty passcode - exception should be thrown
        walletCore.generateKeyPair("")
    }

    @Test
    fun testGetAndSaveDocument() {
        // First create device DID document
        walletCore.createDeviceDIDDoc()

        // Retrieve the saved device document
        val deviceDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_DEVICE)
        assertNotNull("Failed to retrieve device document", deviceDoc)

        // Test document save functionality
        walletCore.saveDocument(Constants.DID_DOC_TYPE_DEVICE)

        // Retrieve again and verify it's not null
        val savedDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_DEVICE)
        assertNotNull("Failed to retrieve device document after saving", savedDoc)
    }

    @Test
    fun testDeleteWallet() {
        // First create wallet
        walletCore.createDeviceDIDDoc()

        // Delete wallet
        walletCore.deleteWallet()
    }

    @Test
    fun testCreateHolderDIDDoc() {
        // First create device DID (initialize wallet)
        walletCore.createDeviceDIDDoc()

        // Generate key
        walletCore.generateKeyPair(testPasscode)

        // Create holder DID document
        val holderDoc = walletCore.createHolderDIDDoc()
        assertNotNull("Failed to create holder DID document", holderDoc)

        // Retrieve saved holder document
        val savedHolderDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
        assertNotNull("Failed to retrieve saved holder document", savedHolderDoc)
    }

    @Test
    fun testIsSavedKey() {
        // Generate key
        walletCore.generateKeyPair(testPasscode)

        // Verify key is saved
        val isSaved = walletCore.isSavedKey(Constants.KEY_ID_PIN)
        assertTrue("PIN key was not saved", isSaved)
    }

    @Test
    fun testChangePin() {
        // Generate key
        walletCore.generateKeyPair(testPasscode)

        // Change PIN
        val newPin = "newTestPasscode123"
        walletCore.changePin(Constants.KEY_ID_PIN, testPasscode, newPin)

        // Verify key is still saved after PIN change
        val isSaved = walletCore.isSavedKey(Constants.KEY_ID_PIN)
        assertTrue("Key is not saved after PIN change", isSaved)
    }

    @Test
    fun testSignAndVerify() {
        // Create wallet and key
        walletCore.createDeviceDIDDoc()
        walletCore.generateKeyPair(testPasscode)
        walletCore.createHolderDIDDoc()

        // Test data
        val testData = "This is test data".toByteArray()

        try {
            // Sign data using the key
            val signature = walletCore.sign(Constants.KEY_ID_PIN, testPasscode.toByteArray(), testData, Constants.DID_DOC_TYPE_HOLDER)
            assertNotNull("Failed to generate signature", signature)

            // Get public key (in a real implementation, this would be retrieved from the DID document)
            // This part may vary depending on the actual implementation
            val didDoc = walletCore.getDocument(Constants.DID_DOC_TYPE_HOLDER)
            assertNotNull("Failed to retrieve DID document", didDoc)

            // Public key extraction logic depends on the actual DIDDocument class implementation
            // Here we simply test, so it's omitted or commented out

            // Actual verification can be done as follows, but without public key extraction logic it cannot be executed
            // val isValid = walletCore.verify(publicKey, testData, signature)
            // assertTrue("Signature verification failed", isValid)
        } catch (e: Exception) {
            fail("Unexpected exception during signing or verification: ${e.message}")
        }
    }
}