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

/**
 * Utilities for going to and from ASCII-HEX representation.
 */
object Base16Upper {

    /**
     * Encodes an array of bytes as hex symbols.
     * @param bytes the array of bytes to encode
     * @return the resulting hex string
     */
    @JvmOverloads
    @JvmStatic
    fun toHex(bytes: ByteArray, separator: String? = null): String =
        bytes.let { toHex(it, 0, it.size, separator) }

    /**
     * Encodes an array of bytes as hex symbols.
     * @param bytes the array of bytes to encode
     * @param offset the start offset in the array of bytes
     * @param length the number of bytes to encode
     * @return the resulting hex string
     */
    @JvmStatic
    fun toHex(bytes: ByteArray, offset: Int, length: Int, separator: String? = null): String =
        StringBuilder().apply {
            (0 until length).forEach { i ->
                bytes[i + offset].toInt().and(0xff).let { unsignedByte ->
                    if (unsignedByte < 16) append("0")
                    append(unsignedByte.toString(16).uppercase())
                    if (separator != null && i + 1 < length) {
                        append(separator)
                    }
                }
            }
        }.toString()

    /**
     * Encodes a single byte to hex symbols.
     * @param b the byte to encode
     * @return the resulting hex string
     */
    @JvmStatic
    fun toHex(b: Byte): String = StringBuilder().also { appendByteAsHex(it, b) }.toString()

    /**
     * Get the byte representation of an ASCII-HEX string.
     * @param hexString The string to convert to bytes
     * @return The byte representation of the ASCII-HEX string.
     */
    @JvmStatic
    fun toBytes(hexString: String): ByteArray = with(hexString) {
        require(length % 2 == 0) { "Input string must contain an even number of characters" }

        val length = length / 2
        return ByteArray(length).also { raw ->
            for (i in 0 until length) {
                val high = Character.digit(this[i * 2], 16)
                val low = Character.digit(this[i * 2 + 1], 16)

                if (high < 0 || low < 0) {
                    throw RuntimeException("Invalid hex digit ${this[i * 2]}${this[i * 2 + 1]}")
                }

                ((high shl 4) or low).let { value ->
                    raw[i] = if (value > 127) (value - 256).toByte() else value.toByte()
                }
            }
        }
    }

    @JvmStatic
    fun toBytesReversed(hexString: String): ByteArray = toBytes(hexString).apply {
        for (i in 0 until size / 2) {
            val temp = this[size - i - 1]
            this[size - i - 1] = this[i]
            this[i] = temp
        }
    }

    @JvmStatic
    fun appendByteAsHex(sb: StringBuilder, b: Byte) {
        b.toInt().and(0xFF).let { unsignedByte ->
            sb.apply {
                if (unsignedByte < 16) append("0")
                append(Integer.toHexString(unsignedByte).uppercase())
            }
        }
    }
}