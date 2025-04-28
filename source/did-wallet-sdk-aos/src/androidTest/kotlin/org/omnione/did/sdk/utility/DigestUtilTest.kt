package org.omnione.did.sdk.utility

import org.junit.Assert.*
import org.junit.Test
import org.omnione.did.sdk.utility.DataModels.DigestEnum
import org.omnione.did.sdk.utility.Errors.UtilityException
import java.util.*

class DigestUtilsTest {

    @Test
    fun testSHA256Digest() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Execute digest function with SHA-256
        val digest = DigestUtils.getDigest(testData, DigestEnum.DIGEST_ENUM.SHA_256)

        // Expected SHA-256 hash of "Hello, World!" (pre-calculated)
        val expectedHash = hexStringToByteArray("dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f")

        // Verify the hash matches expected value
        assertTrue(Arrays.equals(expectedHash, digest))
    }

    @Test
    fun testSHA384Digest() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Execute digest function with SHA-384
        val digest = DigestUtils.getDigest(testData, DigestEnum.DIGEST_ENUM.SHA_384)

        // Expected SHA-384 hash of "Hello, World!" (pre-calculated)
        val expectedHash = hexStringToByteArray("5485cc9b3365b4305dfb4e8337e0a598a574f8242bf17289e0dd6c20a3cd44a089de16ab4ab308f63e44b1170eb5f515")

        // Verify the hash matches expected value
        assertTrue(Arrays.equals(expectedHash, digest))
    }

    @Test
    fun testSHA512Digest() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Execute digest function with SHA-512
        val digest = DigestUtils.getDigest(testData, DigestEnum.DIGEST_ENUM.SHA_512)

        // Expected SHA-512 hash of "Hello, World!" (pre-calculated)
        val expectedHash = hexStringToByteArray("374d794a95cdcfd8b35993185fef9ba368f160d8daf432d08ba9f1ed1e5abe6cc69291e0fa2fe0006a52570ef18c19def4e617c33ce52ef0a6e5fbe318cb0387")

        // Verify the hash matches expected value
        assertTrue(Arrays.equals(expectedHash, digest))
    }

    @Test
    fun testConsistentHashing() {
        // Prepare test data
        val testData = "Consistent hashing test".toByteArray()

        // Execute digest function twice with same input
        val digest1 = DigestUtils.getDigest(testData, DigestEnum.DIGEST_ENUM.SHA_256)
        val digest2 = DigestUtils.getDigest(testData, DigestEnum.DIGEST_ENUM.SHA_256)

        // Verify both digests are identical (hash function is deterministic)
        assertTrue(Arrays.equals(digest1, digest2))
    }

    @Test
    fun testDifferentInputsDifferentHashes() {
        // Prepare two slightly different inputs
        val testData1 = "Test data".toByteArray()
        val testData2 = "Test Data".toByteArray() // Capital 'D'

        // Execute digest function on both inputs
        val digest1 = DigestUtils.getDigest(testData1, DigestEnum.DIGEST_ENUM.SHA_256)
        val digest2 = DigestUtils.getDigest(testData2, DigestEnum.DIGEST_ENUM.SHA_256)

        // Verify hashes are different (even for slightly different inputs)
        assertFalse(Arrays.equals(digest1, digest2))
    }

    @Test(expected = UtilityException::class)
    fun testNullSource() {
        // Attempt to hash null input - should throw UtilityException
        DigestUtils.getDigest(null, DigestEnum.DIGEST_ENUM.SHA_256)
    }

    @Test(expected = UtilityException::class)
    fun testNullDigestEnum() {
        // Attempt to hash with null algorithm - should throw UtilityException
        DigestUtils.getDigest("Test".toByteArray(), null)
    }

    /**
     * Helper method to convert a hex string to byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}