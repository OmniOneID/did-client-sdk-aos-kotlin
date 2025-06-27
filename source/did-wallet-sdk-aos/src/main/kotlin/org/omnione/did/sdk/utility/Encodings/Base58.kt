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

import java.nio.charset.StandardCharsets

/**
 * Base58 is a way to encode and decode data similar to Base64 but designed for Bitcoin addresses.
 */
object Base58 {

    private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val INDEXES = IntArray(128) { -1 }.apply {
        ALPHABET.forEachIndexed { index, c ->
            this[c.code] = index
        }
    }

    /** Encodes the given bytes in Base58. No checksum is appended. */
    @JvmStatic
    fun encode(input: ByteArray): String = input.let {
        if (it.isEmpty()) return ""

        val inputCopy = it.copyOf()
        val zeroCount = inputCopy.takeWhile { byte -> byte == 0.toByte() }.count()

        return ByteArray(inputCopy.size * 2).let { temp ->
            var j = temp.size
            var startAt = zeroCount

            while (startAt < inputCopy.size) {
                val mod = divmod58(inputCopy, startAt)
                if (inputCopy[startAt] == 0.toByte()) startAt++
                temp[--j] = ALPHABET[mod.toInt()].code.toByte()
            }

            // Skip leading zeros in temp array
            while (j < temp.size && temp[j] == ALPHABET[0].code.toByte()) {
                j++
            }

            // Add back the same number of leading zeros
            var remainingZeros = zeroCount
            while (--remainingZeros >= 0) {
                temp[--j] = ALPHABET[0].code.toByte()
            }

            temp.copyOfRange(j, temp.size).toString(StandardCharsets.US_ASCII)
        }
    }

    @JvmStatic
    fun decode(input: String): ByteArray? = input.let {
        if (it.isEmpty()) return ByteArray(0)

        return ByteArray(input.length).also { input58 ->
            // Convert from Base58 characters to indices
            for (i in input.indices) {
                val c = input[i]
                val digit58 = if (c.code in 0 until 128) INDEXES[c.code] else -1
                if (digit58 < 0) return null
                input58[i] = digit58.toByte()
            }

            val zeroCount = input58.takeWhile { byte -> byte == 0.toByte() }.count()

            ByteArray(input.length).let { temp ->
                var j = temp.size
                var startAt = zeroCount

                while (startAt < input58.size) {
                    val mod = divmod256(input58, startAt)
                    if (input58[startAt] == 0.toByte()) startAt++
                    temp[--j] = mod
                }

                // Skip leading zeros in temp array
                while (j < temp.size && temp[j] == 0.toByte()) {
                    j++
                }

                return temp.copyOfRange(j - zeroCount, temp.size)
            }
        }
    }

    @JvmStatic
    private fun divmod58(number: ByteArray, startAt: Int): Byte = with(number) {
        var remainder = 0
        for (i in startAt until size) {
            val digit256 = this[i].toInt() and 0xFF
            val temp = remainder * 256 + digit256
            this[i] = (temp / 58).toByte()
            remainder = temp % 58
        }
        return remainder.toByte()
    }

    @JvmStatic
    private fun divmod256(number58: ByteArray, startAt: Int): Byte = with(number58) {
        var remainder = 0
        for (i in startAt until size) {
            val digit58 = this[i].toInt() and 0xFF
            val temp = remainder * 58 + digit58
            this[i] = (temp / 256).toByte()
            remainder = temp % 256
        }
        return remainder.toByte()
    }
}