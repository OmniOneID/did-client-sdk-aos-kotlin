---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Android Utility SDK API
==

- Subject: CryptoUtils / MultibaseUtils / DigestUtils
- Author: Suhyun Forten Lee
- Date: 2025-03-17
- Version: v1.0.0

| Version | Date       | Changes                 |
| ------ | ---------- | -------------------------|
| v1.0.0 | 2025-03-17 | Initial draft            |


<div style="page-break-after: always;"></div>

# Table of Contents
- [APIs](#api-list)
    - [CryptoUtils](#cryptoutils)
        - [1. generateNonce](#1-generatenonce)
        - [2. generateECKeyPair](#2-generateerkeypair)
        - [3. generateSharedSecret](#3-generatesharedsecret)
        - [4. pbkdf2](#4-pbkdf2)
        - [5. encrypt](#5-encrypt)
        - [6. decrypt](#6-decrypt)
    - [MultibaseUtils](#multibaseutils)
        - [1. encode](#1-encode)
        - [2. decode](#2-decode)
    - [DigestUtils](#digetutils)
        - [1. getDigest](#1-getdigest) 
- [Enumerators](#enumerators)
    - [1. EC_TYPE](#1-ec_type)
    - [2. ENCRYPTION_TYPE](#2-encryption_type)
    - [3. ENCRYPTION_MODE](#3-encryption_mode)
    - [4. SYMMETRIC_KEY_SIZE](#4-symmetric_key_size)
    - [5. ENCRYPTION_TYPE(SYMMETRIC)](#5-encryption_type)
    - [6. SYMMETRIC_PADDING_TYPE](#6-symmetric_padding_type)
    - [7. MULTIBASE_TYPE](#7-multibase_type)
    - [8. DIGEST_ENUM](#8-digest_enum)
- [Value Object](#value-object)
    - [1. EcKeyPair](#1-eckeypair)
    - [2. CipherInfo](#2-cipherinfo)


# API List
## CryptoUtils
Class for encryption-related functionality

### 1. generateNonce

#### Description
`Generates a random nonce.`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateNonce(size: Int): ByteArray
```


#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| size    | Int    | nonce length |M| |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | nonce |M| |


#### Usage
```java
byte[] salt = CryptoUtils.generateNonce(20);
```

<br>

### 2. generateECKeyPair

#### Description
`Generates a key pair for ECDH.`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateECKeyPair(ecType: EcType.EC_TYPE): EcKeyPair
```

#### Parameters

| Name      | Type   | Description                | **M/O** | **Note**            |
|-----------|--------|----------------------------|---------|---------------------|
| ecType | EcType.EC_TYPE | EC algorithm type          | M       | [EC_TYPE](#1-ec_type) |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| EcKeyPair | Key pair object               | M       | |



#### Usage
```java
EcKeyPair dhKeyPair = CryptoUtils.generateECKeyPair(EcType.EC_TYPE.SECP256_R1);
```

<br>

### 3. generateSharedSecret

#### Description
`Generates an ECDH shared secret key`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateSharedSecret(ecType: EcType.EC_TYPE, privateKey: ByteArray?, publicKey: ByteArray?): ByteArray
```

#### Parameters

| Name      | Type   | Description                | **M/O** | **Note**            |
|-----------|--------|----------------------------|---------|---------------------|
| ecType | EcType.EC_TYPE | EC algorithm type          | M       | [EC_TYPE](#1-ec_type) |
| privateKey | ByteArray | Private key          | M       |  |
| publicKey | ByteArray | Public key          | M       |  |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Shared Secret |M| |



#### Usage
```java
byte[] privKey = MultibaseUtils.decode("zHr5d9pyMRnyz2aByr6dYdV5kdfnWRUkiFxjSaoFJwecs");
byte[] serverPubKey = MultibaseUtils.decode("f02eb2044610ba2c3960f9d91196bdb1c5498beebcb04983ed6ebe2329c3612907c");

byte[] sharedSecret = CryptoUtils.generateSharedSecret(EcType.EC_TYPE.SECP256_R1, privKey, serverPubKey );
```

<br>

### 4. pbkdf2

#### Description
`Derives a key from a password using the PBKDF2 algorithm`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun pbkdf2(password: ByteArray?, salt: ByteArray?, iterations: Int, derivedKeyLength: Int): ByteArray
```


#### Parameters
| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| password    | ByteArray    | Seed data |M| |
| salt    | ByteArray | Salt |    M    |  |
| iterations    | Int | Iterations |    M    | |
| derivedKeyLength   | Int  | Length of the derived key  |    M    |  |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Derived data |M| |


#### Usage
```java
byte[] salt = CryptoUtils.generateNonce(20);
byte[] symmetricKey = CryptoUtils.pbkdf2("password".getByte(), salt, 2048, 48);          
```

<br>

### 5. encrypt

#### Description
`Encryption`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun encrypt(plain: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| plain    | ByteArray    | Data to encrypt |M| |
| info    | CipherInfo | Encryption information |    M    | [CipherInfo](#2-cipherinfo) |
| key    | ByteArray | Symmetric key |    M    | |
| iv   | ByteArray  | Initialization vector  |    M    |  |

#### Returns


| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Encrypted data |M| |


#### Usage
```java
byte[] encData = CryptoUtils.encrypt(
                    plainData,
                    new CipherInfo(CipherInfo.ENCRYPTION_TYPE.AES, CipherInfo.ENCRYPTION_MODE.CBC, CipherInfo.SYMMETRIC_KEY_SIZE.AES_256, CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5),
                    MultibaseUtils.decode("fbfaee9e0506786f61d2a97e29d034ae47955c68791049a6377ebe8d1bedb7ef4"),
                    MultibaseUtils.decode("z75M7MfQsC4p2rTxeKxYh2M")
                    );
```

<br>

### 6. decrypt

#### Description
`Decryption`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decrypt(ciphertext: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| cipher    | ByteArray    | Encrypted data |M| |
| info    | CipherInfo | Encryption information |    M    | [CipherInfo](#2-cipherinfo) |
| key    | ByteArray | Symmetric key |    M    | |
| iv   | ByteArray  | Initialization vector  |    M    |  |


#### Returns
| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Decrypted data |M| |

#### Usage
```java
byte[] decData = CryptoUtils.decrypt(
                    cipher,
                    new CipherInfo(CipherInfo.ENCRYPTION_TYPE.AES, CipherInfo.ENCRYPTION_MODE.CBC, CipherInfo.SYMMETRIC_KEY_SIZE.AES_256, CipherInfo.SYMMETRIC_PADDING_TYPE.PKCS5),
                    MultibaseUtils.decode("fbfaee9e0506786f61d2a97e29d034ae47955c68791049a6377ebe8d1bedb7ef4"),
                    MultibaseUtils.decode("z75M7MfQsC4p2rTxeKxYh2M")
                    );
```

<br>

## MultibaseUtils
Multibase encoding/decoding class

### 1. encode

#### Description
`Multibase encoding`

#### Declaration

```kotlin
@JvmStatic
fun encode(type: MultibaseType.MULTIBASE_TYPE, data: ByteArray?): String
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| type    | MultibaseType.MULTIBASE_TYPE    | Multibase type |M|  [MULTIBASE_TYPE](#7-multibase_type) |
| data    | ByteArray    | Data to encode |M|  |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| String  | Encoded string |M| |


#### Usage
```java
 String secretStr = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16, sharedSecret);
```

<br>

### 2. decode

#### Description
`Decodes an encoded string`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decode(encoded: String): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| encoded    | String    | Encoded string |M|  |

#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Decoded data |M| |



#### Usage
```java
byte[] salt = MultibaseUtils.decode("f6c646576656c6f7065726c3139383540676d61696c2e636f6d");
```

<br>

## DigestUtils
Hash utility class

### 1. getDigest

#### Description
`Hash`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun getDigest(source: ByteArray?, digestEnum: DigestEnum.DIGEST_ENUM?): ByteArray 
```


#### Parameters

| Parameter | Type   | Description                | **M/O** | **Note** |
|-----------|--------|----------------------------|---------|---------|
| source    | ByteArray    |  Data to hash |M| |
| digestEnum    | DigestEnum.DIGEST_ENUM    |  Hash algorithm |M|[DIGEST_ENUM](#8-digest_enum)|


#### Returns

| Type | Description                |**M/O** | **Note** |
|------|----------------------------|---------|---------|
| ByteArray  | Hashed data |M| |


#### Usage
```java
byte[] digest = DigestUtils.getDigest(plainData, DigestEnum.DIGEST_ENUM.SHA_256);
```

<br>


# Enumerators
## 1. EC_TYPE

### Description

`EC key algorithm type`

### Declaration

```kotlin
class EcType {
    enum class EC_TYPE(val value: String) {
        SECP256_K1("Secp256k1"),
        SECP256_R1("Secp256r1");
..
```
<br>

## 2. ENCRYPTION_TYPE

### Description

`Encryption type`

### Declaration

```kotlin
class DigestEnum {
    enum class DIGEST_ENUM(val value: String) {
        SHA_256("SHA256"),
        SHA_384("SHA384"),
        SHA_512("SHA512");
..
```
<br>

## 3. ENCRYPTION_MODE

### Description

`Operation mode`

### Declaration

```kotlin
..
    enum class ENCRYPTION_MODE(val value: String) {
        CBC("CBC"),
        ECB("ECB");
..
```
<br>


## 4. SYMMETRIC_KEY_SIZE

### Description

`Symmetric key length`

### Declaration

```kotlin
..
    enum class SYMMETRIC_KEY_SIZE(val value: String) {
        AES_128("128"),
        AES_256("256");
..
```
<br>

## 5. ENCRYPTION_TYPE(SYMMETRIC)

### Description

`Encryption/decryption type`

### Declaration

```kotlin
..
    enum class ENCRYPTION_TYPE(val value: String) {
        AES("AES");
..
```
<br>

## 6. SYMMETRIC_PADDING_TYPE

### Description

`Padding type`

### Declaration

```kotlin
..
    enum class SYMMETRIC_PADDING_TYPE(val key: String, val value: String) {
        NOPAD("NOPAD", "NOPAD"),
        PKCS5("PKCS5", "PKCS5Padding");
..
```
<br>

## 7. MULTIBASE_TYPE

### Description

`Multibase encoding type`

### Declaration

```kotlin
class MultibaseType {
    enum class MULTIBASE_TYPE(val value: String) {
        BASE_16("f"),
        BASE_16_UPPER("F"),
        BASE_58_BTC("z"),
        BASE_64("m"),
        BASE_64_URL("u");
..
```
<br>

## 8. DIGEST_ENUM

### Description

`Hash algorithm`

### Declaration

```kotlin
class DigestEnum {
    enum class DIGEST_ENUM(val value: String) {
        SHA_256("SHA256"),
        SHA_384("SHA384"),
        SHA_512("SHA512");
..
```
<br>

# Value Object

## 1. EcKeyPair

### Description

`EC key pair information`

### Declaration

```kotlin
data class EcKeyPair(
    @SerializedName("ecType") @Expose var ecType: EcType? = null,
    @SerializedName("privateKey") @Expose var privateKey: String? = null,
    @SerializedName("publicKey") @Expose var publicKey: String? = null
) {
..
```
### Property

| Name          | Type               | Description                      | **M/O** | **Note**                    |
|---------------|--------------------|----------------------------------|---------|-----------------------------|
| ecType | EC_TYPE | EC algorithm type          | M       | [EC_TYPE](#1-ec_type) |
| privateKey | String | Private key          | M       |  |
| publicKey | String | Public key          | M       |  |

<br>

## 2. CipherInfo

### Description

`Encryption information`

### Declaration

```kotlin
data class CipherInfo(
    var type: ENCRYPTION_TYPE? = null,
    var mode: ENCRYPTION_MODE? = null,
    var size: SYMMETRIC_KEY_SIZE? = null,
    var padding: SYMMETRIC_PADDING_TYPE? = null
) {
..
```

### Property

| Name          | Type               | Description                      | **M/O** | **Note**                    |
|---------------|--------------------|----------------------------------|---------|-----------------------------|
| type | ENCRYPTION_TYPE | Encryption type          | M       | [ENCRYPTION_TYPE](#2-encryption_type) |
| mode | ENCRYPTION_MODE | Operation mode          | M       | [ENCRYPTION_MODE](#3-encryption_mode) |
| size | SYMMETRIC_KEY_SIZE | Symmetric key length         | M       | [SYMMETRIC_KEY_SIZE](#4-symmetric_key_size) |
| padding | SYMMETRIC_PADDING_TYPE | Padding          | M       | [SYMMETRIC_PADDING_TYPE](#6-symmetric_padding_type) |
