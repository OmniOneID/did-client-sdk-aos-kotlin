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

- 주제: CryptoUtils / MultibaseUtils / DigestUtils
- 작성: Suhyun Forten Lee
- 일자: 2025-03-17
- 버전: v1.0.0

| 버전   | 일자       | 변경 내용                 |
| ------ | ---------- | -------------------------|
| v1.0.0 | 2025-03-17 | 초기 작성                 |


<div style="page-break-after: always;"></div>

# 목차
- [APIs](#api-목록)
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


# API 목록
## CryptoUtils
암호화 기능 관련 클래스

### 1. generateNonce

#### Description
`랜덤한 nonce 생성.`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateNonce(size: Int): ByteArray
```


#### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| size    | Int    | nonce length |M| |

#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | nonce |M| |


#### Usage
```java
byte[] salt = CryptoUtils.generateNonce(20);
```

<br>

### 2. generateECKeyPair

#### Description
`ECDH 용도의 키 쌍 생성.`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateECKeyPair(ecType: EcType.EC_TYPE): EcKeyPair
```

#### Parameters

| Name      | Type   | Description                | **M/O** | **Note**            |
|-----------|--------|----------------------------|---------|---------------------|
| ecType | EcType.EC_TYPE | EC 알고리즘 타입          | M       | [EC_TYPE](#1-ec_type) |

#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| EcKeyPair | 키쌍 객체               | M       | |



#### Usage
```java
EcKeyPair dhKeyPair = CryptoUtils.generateECKeyPair(EcType.EC_TYPE.SECP256_R1);
```

<br>

### 3. generateSharedSecret

#### Description
`ECDH 비밀 공유키 생성`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun generateSharedSecret(ecType: EcType.EC_TYPE, privateKey: ByteArray?, publicKey: ByteArray?): ByteArray
```

#### Parameters

| Name      | Type   | Description                | **M/O** | **Note**            |
|-----------|--------|----------------------------|---------|---------------------|
| ecType | EcType.EC_TYPE | EC 알고리즘 타입          | M       | [EC_TYPE](#1-ec_type) |
| privateKey | ByteArray | 개인키          | M       |  |
| publicKey | ByteArray | 공개키          | M       |  |

#### Returns

| Type | Description                |**M/O** | **비고** |
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
`PBKDF2 알고리즘으로 password 기반으로 키 파생`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun pbkdf2(password: ByteArray?, salt: ByteArray?, iterations: Int, derivedKeyLength: Int): ByteArray
```


#### Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| password    | ByteArray    | seed 데이터 |M| |
| salt    | ByteArray | salt |    M    |  |
| iterations    | Int | iterations |    M    | |
| derivedKeyLength   | Int  | 파생시킬 키의 길이  |    M    |  |

#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 파생된 데이터 |M| |


#### Usage
```java
byte[] salt = CryptoUtils.generateNonce(20);
byte[] symmetricKey = CryptoUtils.pbkdf2("password".getByte(), salt, 2048, 48);          
```

<br>

### 5. encrypt

#### Description
`암호화 `

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun encrypt(plain: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| plain    | ByteArray    | 암호화 할 데이터 |M| |
| info    | CipherInfo | 암호화 정보 |    M    | [CipherInfo](#2-cipherinfo) |
| key    | ByteArray | 대칭키 |    M    | |
| iv   | ByteArray  | iv  |    M    |  |

#### Returns


| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 암호화된 데이터 |M| |


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
`복호화`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decrypt(ciphertext: ByteArray, info: CipherInfo, key: ByteArray, iv: ByteArray?): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| cipher    | ByteArray    | 암호화 된 데이터 |M| |
| info    | CipherInfo | 암호화 정보 |    M    | [CipherInfo](#2-cipherinfo) |
| key    | ByteArray | 대칭키 |    M    | |
| iv   | ByteArray  | iv  |    M    |  |


#### Returns
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 복호화 된 데이터 |M| |

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
Multibase 인코딩/디코딩 클래스

### 1. encode

#### Description
`Multibase 인코딩`

#### Declaration

```kotlin
@JvmStatic
fun encode(type: MultibaseType.MULTIBASE_TYPE, data: ByteArray?): String
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| type    | MultibaseType.MULTIBASE_TYPE    | Multibase 타입 |M|  [MULTIBASE_TYPE](#7-multibase_type) |
| data    | ByteArray    | 인코딩 할 데이터 |M|  |

#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| String  | 인코딩 된 문자열 |M| |


#### Usage
```java
 String secretStr = MultibaseUtils.encode(MultibaseType.MULTIBASE_TYPE.BASE_16, sharedSecret);
```

<br>

### 2. decode

#### Description
`인코딩 된 문자열`

#### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decode(encoded: String): ByteArray
```

#### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| encoded    | String    | 인코딩 된 문자열 |M|  |

#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 디코딩 된 데이터 |M| |



#### Usage
```java
byte[] salt = MultibaseUtils.decode("f6c646576656c6f7065726c3139383540676d61696c2e636f6d");
```

<br>

## DigestUtils
Hash 기능 유틸 클래스

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

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| source    | ByteArray    |  해시할 데이터 |M| |
| digestEnum    | DigestEnum.DIGEST_ENUM    |  해시 알고리즘 |M|[DIGEST_ENUM](#8-digest_enum)|


#### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 해시 된 데이터 |M| |


#### Usage
```java
byte[] digest = DigestUtils.getDigest(plainData, DigestEnum.DIGEST_ENUM.SHA_256);
```

<br>


# Enumerators
## 1. EC_TYPE

### Description

`EC키 알고리즘 타입`

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

`암호화 타입`

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

`운용모드`

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

`대칭키 길이`

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

`암복호화 타입`

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

`패딩 타입`

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

`Multibase 인코딩 타입`

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

`해시 알고리즘`

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

`EC키쌍 정보`

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
| ecType | EC_TYPE | EC 알고리즘 타입          | M       | [EC_TYPE](#1-ec_type) |
| privateKey | String | 개인키          | M       |  |
| publicKey | String | 공개키          | M       |  |

<br>

## 2. CipherInfo

### Description

`암호화 정보`

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
| type | ENCRYPTION_TYPE | 암호화 타입          | M       | [ENCRYPTION_TYPE](#2-encryption_type) |
| mode | ENCRYPTION_MODE | 운용모드          | M       | [ENCRYPTION_MODE](#3-encryption_mode) |
| size | SYMMETRIC_KEY_SIZE | 대칭키 길이         | M       | [SYMMETRIC_KEY_SIZE](#4-symmetric_key_size) |
| padding | SYMMETRIC_PADDING_TYPE | 패딩          | M       | [SYMMETRIC_PADDING_TYPE](#6-symmetric_padding_type) |
