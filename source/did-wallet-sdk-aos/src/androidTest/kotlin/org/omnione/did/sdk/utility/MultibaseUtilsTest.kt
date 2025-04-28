package org.omnione.did.sdk.utility

import org.junit.Assert.*
import org.junit.Test
import org.omnione.did.sdk.utility.DataModels.MultibaseType
import org.omnione.did.sdk.utility.Errors.UtilityException
import java.util.*

class MultibaseUtilsTest {

    @Test
    fun testEncodeDecodeBase16() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Encode using BASE_16
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16, testData)

        // Verify encoded string starts with the correct prefix
        assertTrue(encoded.startsWith(MultibaseType.MULTIBASE_TYPE.BASE_16.value))

        // Decode the encoded string
        val decoded = MultibaseUtils.decode(encoded)

        // Verify the round trip
        assertTrue(Arrays.equals(testData, decoded))
    }

    @Test
    fun testEncodeDecodeBase16Upper() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Encode using BASE_16_UPPER
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER, testData)

        // Verify encoded string starts with the correct prefix
        assertTrue(encoded.startsWith(MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER.value))

        // Verify the encoded content is uppercase (excluding the prefix)
        val content = encoded.substring(1)
        assertEquals(content, content.uppercase())

        // Decode the encoded string
        val decoded = MultibaseUtils.decode(encoded)

        // Verify the round trip
        assertTrue(Arrays.equals(testData, decoded))
    }

    @Test
    fun testEncodeDecodeBase58BTC() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Encode using BASE_58_BTC
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_58_BTC, testData)

        // Verify encoded string starts with the correct prefix
        assertTrue(encoded.startsWith(MultibaseType.MULTIBASE_TYPE.BASE_58_BTC.value))

        // Decode the encoded string
        val decoded = MultibaseUtils.decode(encoded)

        // Verify the round trip
        assertTrue(Arrays.equals(testData, decoded))
    }

    @Test
    fun testEncodeDecodeBase64() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Encode using BASE_64
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, testData)

        // Verify encoded string starts with the correct prefix
        assertTrue(encoded.startsWith(MultibaseType.MULTIBASE_TYPE.BASE_64.value))

        // Decode the encoded string
        val decoded = MultibaseUtils.decode(encoded)

        // Verify the round trip
        assertTrue(Arrays.equals(testData, decoded))
    }

    @Test
    fun testEncodeDecodeBase64URL() {
        // Prepare test data
        val testData = "Hello, World!".toByteArray()

        // Encode using BASE_64_URL
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64_URL, testData)

        // Verify encoded string starts with the correct prefix
        assertTrue(encoded.startsWith(MultibaseType.MULTIBASE_TYPE.BASE_64_URL.value))

        // Verify the encoded content does not contain characters that need to be URL-escaped
        val content = encoded.substring(1)
        assertFalse(content.contains("+"))
        assertFalse(content.contains("/"))

        // Decode the encoded string
        val decoded = MultibaseUtils.decode(encoded)

        // Verify the round trip
        assertTrue(Arrays.equals(testData, decoded))
    }

    @Test
    fun testEncodeWithNullData() {
        // Encode null data
        val encoded = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_64, null)

        // Verify the result is an empty string
        assertEquals("", encoded)
    }

    @Test(expected = UtilityException::class)
    fun testDecodeWithInvalidInput() {
        // Attempt to decode an invalid input (too short)
        MultibaseUtils.decode("a")
    }

    @Test(expected = UtilityException::class)
    fun testDecodeWithInvalidPrefix() {
        // Attempt to decode with an invalid multibase prefix
        // Assuming "X" is not a valid multibase prefix
        MultibaseUtils.decode("XInvalidContent")
    }

    @Test
    fun testAllEncodingFormats() {
        // Test all encoding formats with various data sizes

        val testDataSets = arrayOf(
            "A".toByteArray(),                  // Single character
            "Hello".toByteArray(),              // Short string
            "Hello, World!".toByteArray(),      // Medium string
            ByteArray(100) { it.toByte() }      // Binary data
        )

        val allFormats = arrayOf(
            MultibaseType.MULTIBASE_TYPE.BASE_16_UPPER,
            MultibaseType.MULTIBASE_TYPE.BASE_58_BTC,
            MultibaseType.MULTIBASE_TYPE.BASE_64,
            MultibaseType.MULTIBASE_TYPE.BASE_64_URL
        )

        for (data in testDataSets) {
            for (format in allFormats) {
                // Encode
                val encoded = MultibaseUtils.encode(format, data)
                // Decode
                val decoded = MultibaseUtils.decode(encoded)
                // Verify
                assertTrue(
                    "Failed for format ${format.name} with data length ${data.size}",
                    Arrays.equals(data, decoded)
                )
            }
        }
    }
}