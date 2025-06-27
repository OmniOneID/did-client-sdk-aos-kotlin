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

- 주제: SecureEncryptor
- 작성: Sangjun Kim
- 일자: 2025-03-13
- 버전: v1.0.0

| 버전   | 일자       | 변경 내용                 |
| ------ | ---------- | -------------------------|
| v1.0.0 | 2025-03-13 | 초기 작성                 |


<div style="page-break-after: always;"></div>

# 목차
- [APIs](#api-목록)
    - [1. encrypt](#1-encrypt)
    - [2. decrypt](#2-decrypt)


# API 목록
## 1. encryptor

### Description
`Keystore의 암호화키를 통하여 데이터를 암호화한다.`

### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun encrypt(plainData: ByteArray?, context: Context?): ByteArray
```


### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| plainData    | ByteArray    | 암호화 할 데이터 |M| |
| context    | Context | context |M| Keystore에 키가없는 경우 생성할때 필요함 |

### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 암호화데이터 |M| |


### Usage
```java
byte[] encData = SecureEncryptor.encrypt("plainData".getBytes(), context);
```

<br>

## 2. decrypt

### Description
`Keystore의 복호화키를 통하여 데이터를 복호화한다.`

### Declaration

```kotlin
@JvmStatic
@Throws(Exception::class)
fun decrypt(cipherData: ByteArray?): ByteArray
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| cipherData    | ByteArray    | 암호화 데이터 |M| |

### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| ByteArray  | 복호화데이터 |M| |



### Usage
```java
byte[] decData = SecureEncryptor.decrypt(cipherData);
```

<br>
