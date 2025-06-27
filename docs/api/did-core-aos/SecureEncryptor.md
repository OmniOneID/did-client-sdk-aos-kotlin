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

Android SecureEncryptor Core API
==

- Topic: SecureEncryptor
- Author: Sangjun Kim
- Date: 2025-03-13
- Version: v1.0.0

| Version | Date       | Change Details            |
| ------- | ---------- | ------------------------- |
| v1.0.0  | 2025-03-13 | Initial version           |


<div style="page-break-after: always;"></div>

# Table of Contents
- [APIs](#api-list)
    - [1. encrypt](#1-encrypt)
    - [2. decrypt](#2-decrypt)

# APIs
## 1. encryptor

### Description
`Encrypts data using the encryption key from Keystore.`

### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun encrypt(plainData: ByteArray?, context: Context?): ByteArray
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **Remarks** |
|-----------|--------|----------------------------|---------|-------------|
| plainData | ByteArray | Data to be encrypted       | M       |             |
| context   | Context| Context                    | M       | Needed to create a key in Keystore if not present |

### Returns

| Type   | Description           | **M/O** | **Remarks** |
|--------|-----------------------|---------|-------------|
| ByteArray | Encrypted data        | M       |             |


### Usage
```java
byte[] encData = SecureEncryptor.encrypt("plainData".getBytes(), context);
```

<br>

## 2. decrypt

### Description
`Decrypts data using the decryption key from Keystore.`

### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decrypt(cipherData: ByteArray?): ByteArray
```

### Parameters

| Parameter  | Type   | Description      | **M/O** | **Remarks** |
|------------|--------|------------------|---------|-------------|
| cipherData | ByteArray | Encrypted data   | M       |             |

### Returns

| Type   | Description         | **M/O** | **Remarks** |
|--------|---------------------|---------|-------------|
| ByteArray | Decrypted data      | M       |             |



### Usage
```java
byte[] decData = SecureEncryptor.decrypt(cipherData);
```

<br>