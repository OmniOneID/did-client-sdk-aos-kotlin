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

package org.omnione.did.sdk.utility.Encodings

import java.io.UnsupportedEncodingException
import java.util.Base64

/**
 * Utilities for encoding and decoding the Base64 representation of binary data. See RFCs
 * [2045](http://www.ietf.org/rfc/rfc2045.txt) and [3548](http://www.ietf.org/rfc/rfc3548.txt).
 */
object Base64 {
    abstract class Coder {
        lateinit var output: ByteArray
        var op: Int = 0

        abstract fun maxOutputSize(len: Int): Int
        abstract fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean
    }

    class Decoder(flags: Int, output: ByteArray) : Coder() {
        private val DECODE = intArrayOf(
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1,
            -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
        )
        private val DECODE_WEBSAFE = DECODE.copyOf()

        private var state: Int = 0
        private var value: Int = 0
        private val alphabet: IntArray = if (flags and URL_SAFE == 0) DECODE else DECODE_WEBSAFE

        init {
            this.output = output
        }

        override fun maxOutputSize(len: Int): Int = len * 3 / 4 + 10

        override fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean =
            when (state) {
                6 -> false
                else -> with(input) {
                    var p = offset
                    val end = len + offset
                    var op = 0

                    while (p < end) {
                        alphabet[this[p++].toInt() and 0xff].let { d ->
                            when (state) {
                                0 -> if (d >= 0) {
                                    value = d
                                    state++
                                }
                                1 -> if (d >= 0) {
                                    value = (value shl 6) or d
                                    state++
                                }
                                2 -> if (d >= 0) {
                                    value = (value shl 6) or d
                                    state++
                                }
                                3 -> if (d >= 0) {
                                    value = (value shl 6) or d
                                    output.apply {
                                        this[op++] = (value shr 16).toByte()
                                        this[op++] = (value shr 8).toByte()
                                        this[op++] = value.toByte()
                                    }
                                    state = 0
                                }
                            }
                        }
                    }
                    true
                }
            }
    }

    class Encoder(flags: Int, output: ByteArray) : Coder() {
        private val ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toByteArray()
        private val ENCODE_WEBSAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toByteArray()

        private val alphabet = if (flags and URL_SAFE == 0) ENCODE else ENCODE_WEBSAFE

        init {
            this.output = output
        }

        override fun maxOutputSize(len: Int): Int = len * 8 / 5 + 10

        override fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean =
            with(input) {
                var p = offset
                val end = len + offset
                var op = 0

                while (p < end) {
                    val v = (this[p++].toInt() and 0xff) shl 16 or
                            (if (p < end) (this[p++].toInt() and 0xff) shl 8 else 0) or
                            (if (p < end) (this[p++].toInt() and 0xff) else 0)

                    output.apply {
                        this[op++] = alphabet[v shr 18 and 0x3f]
                        this[op++] = alphabet[v shr 12 and 0x3f]
                        this[op++] = alphabet[v shr 6 and 0x3f]
                        this[op++] = alphabet[v and 0x3f]
                    }
                }
                true
            }
    }

    // Default values for encoder/decoder flags
    const val DEFAULT = 0
    const val NO_PADDING = 1
    const val NO_WRAP = 2
    const val CRLF = 4
    const val URL_SAFE = 8

    /**
     * Decodes a Base64-encoded byte array.
     *
     * @param input The input array to decode.
     * @param flags Controls certain features of the decoded output.
     * @throws IllegalArgumentException If the input contains incorrect padding.
     * @return The decoded byte array.
     */
    @JvmStatic
    fun decode(input: ByteArray, flags: Int = DEFAULT): ByteArray =
        input.let { decode(it, 0, it.size, flags) }

    /**
     * Decodes a Base64-encoded byte array with a specified offset and length.
     *
     * @param input The data to decode.
     * @param offset The starting position within the input array.
     * @param len The number of bytes to decode.
     * @param flags Controls decoding features.
     * @throws IllegalArgumentException If decoding fails.
     * @return The decoded byte array.
     */
    @JvmStatic
    fun decode(input: ByteArray, offset: Int, len: Int, flags: Int): ByteArray =
        input.copyOfRange(offset, offset + len).let { subArray ->
            when (flags) {
                URL_SAFE -> java.util.Base64.getUrlDecoder().decode(subArray)
                else -> java.util.Base64.getDecoder().decode(subArray)
            }
        }

    /**
     * Decodes a Base64-encoded string.
     *
     * @param str The input string to decode.
     * @param flags Controls decoding features.
     * @throws IllegalArgumentException If decoding fails.
     * @return The decoded byte array.
     */
    @JvmStatic
    fun decode(str: String, flags: Int = DEFAULT): ByteArray =
        str.toByteArray().let { decode(it, flags) }

    /**
     * Encodes a byte array into Base64 format.
     *
     * @param input The data to encode.
     * @param flags Controls encoding features.
     * @return The encoded byte array.
     */
    @JvmStatic
    fun encode(input: ByteArray, flags: Int = DEFAULT): ByteArray =
        input.let { encode(it, 0, it.size, flags) }

    /**
     * Encodes a byte array into Base64 format with a specified offset and length.
     *
     * @param input The data to encode.
     * @param offset The starting position within the input array.
     * @param len The number of bytes to encode.
     * @param flags Controls encoding features.
     * @return The encoded byte array.
     */
    @JvmStatic
    fun encode(input: ByteArray, offset: Int, len: Int, flags: Int): ByteArray =
        input.copyOfRange(offset, offset + len).let { subArray ->
            when (flags) {
                NO_PADDING -> java.util.Base64.getEncoder().withoutPadding().encode(subArray)
                URL_SAFE -> java.util.Base64.getUrlEncoder().encode(subArray)
                NO_WRAP -> java.util.Base64.getEncoder().encode(subArray) // Default encoder does not wrap
                else -> java.util.Base64.getEncoder().encode(subArray)
            }
        }

    /**
     * Encodes a byte array into a Base64 string.
     *
     * @param input The data to encode.
     * @param flags Controls encoding features.
     * @return The encoded string.
     */
    @JvmStatic
    fun encodeToString(input: ByteArray, flags: Int = DEFAULT): String =
        input.let {
            when (flags) {
                NO_PADDING -> java.util.Base64.getEncoder().withoutPadding().encodeToString(it)
                URL_SAFE -> java.util.Base64.getUrlEncoder().encodeToString(it)
                NO_WRAP -> java.util.Base64.getEncoder().encodeToString(it) // Default encoder does not wrap
                else -> java.util.Base64.getEncoder().encodeToString(it)
            }
        }

    /**
     * Encodes a byte array into a URL-safe Base64 string.
     *
     * @param input The data to encode.
     * @return The URL-safe Base64 encoded string.
     */
    @JvmStatic
    fun encodeUrlString(input: ByteArray): String =
        java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(input)

    /**
     * Decodes a URL-safe Base64 string into a byte array.
     *
     * @param input The Base64-encoded string.
     * @return The decoded byte array.
     */
    @JvmStatic
    fun decodeUrl(input: String): ByteArray =
        java.util.Base64.getUrlDecoder().decode(input)
}