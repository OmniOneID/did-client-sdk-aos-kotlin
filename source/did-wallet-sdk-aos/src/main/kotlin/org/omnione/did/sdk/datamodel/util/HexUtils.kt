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

package org.omnione.did.sdk.datamodel.util

/**
 * Utilities for going to and from ASCII-HEX representation.
 */
class HexUtils {
	companion object {
		/**
		 * Encodes an array of bytes as hex symbols.
		 * @param bytes the array of bytes to encode
		 * @return the resulting hex string
		 */
		@JvmStatic
		fun toHex(bytes: ByteArray): String {
			return toHex(bytes, 0, bytes.size, null)
		}

		/**
		 * Encodes an array of bytes as hex symbols.
		 * @param bytes the array of bytes to encode
		 * @param separator the separator to use between two bytes, can be null
		 * @return the resulting hex string
		 */
		@JvmStatic
		fun toHex(bytes: ByteArray, separator: String?): String {
			return toHex(bytes, 0, bytes.size, separator)
		}

		/**
		 * Encodes an array of bytes as hex symbols.
		 * @param bytes the array of bytes to encode
		 * @param offset the start offset in the array of bytes
		 * @param length the number of bytes to encode
		 * @return the resulting hex string
		 */
		@JvmStatic
		fun toHex(bytes: ByteArray, offset: Int, length: Int): String {
			return toHex(bytes, offset, length, null)
		}

		/**
		 * Encodes a single byte to hex symbols.
		 * @param b the byte to encode
		 * @return the resulting hex string
		 */
		@JvmStatic
		fun toHex(b: Byte): String {
			val sb = StringBuilder()
			appendByteAsHex(sb, b)
			return sb.toString()
		}

		/**
		 * Encodes an array of bytes as hex symbols.
		 * @param bytes the array of bytes to encode
		 * @param offset the start offset in the array of bytes
		 * @param length the number of bytes to encode
		 * @param separator the separator to use between two bytes, can be null
		 * @return the resulting hex string
		 */
		@JvmStatic
		fun toHex(bytes: ByteArray, offset: Int, length: Int, separator: String?): String {
			val result = StringBuilder()
			for (i in 0 until length) {
				val unsignedByte = bytes[i + offset].toInt() and 0xff

				if (unsignedByte < 16) {
					result.append("0")
				}

				result.append(Integer.toHexString(unsignedByte))
				if (separator != null && i + 1 < length) {
					result.append(separator)
				}
			}
			return result.toString()
		}

		/**
		 * Get the byte representation of an ASCII-HEX string.
		 * @param hexString The string to convert to bytes
		 * @return The byte representation of the ASCII-HEX string.
		 */
		@JvmStatic
		fun toBytes(hexString: String): ByteArray {
			if (hexString.length % 2 != 0) {
				throw RuntimeException("Input string must contain an even number of characters")
			}
			val hex = hexString.toCharArray()
			val length = hex.size / 2
			val raw = ByteArray(length)
			for (i in 0 until length) {
				val high = Character.digit(hex[i * 2], 16)
				val low = Character.digit(hex[i * 2 + 1], 16)
				if (high < 0 || low < 0) {
					throw RuntimeException("Invalid hex digit ${hex[i * 2]}${hex[i * 2 + 1]}")
				}
				var value = (high shl 4) or low
				if (value > 127) value -= 256
				raw[i] = value.toByte()
			}
			return raw
		}

		@JvmStatic
		fun toBytesReversed(hexString: String): ByteArray {
			val rawBytes = toBytes(hexString)

			for (i in 0 until rawBytes.size / 2) {
				val temp = rawBytes[rawBytes.size - i - 1]
				rawBytes[rawBytes.size - i - 1] = rawBytes[i]
				rawBytes[i] = temp
			}

			return rawBytes
		}

		private fun appendByteAsHex(sb: StringBuilder, b: Byte) {
			val unsignedByte = b.toInt() and 0xFF
			if (unsignedByte < 16) {
				sb.append("0")
			}
			sb.append(Integer.toHexString(unsignedByte))
		}
	}
}

