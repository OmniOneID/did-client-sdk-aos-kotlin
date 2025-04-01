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

Android Wallet SDK API
==

- Subject: WalletAPI
- Writer: Suhyun Forten Lee
- Date: 2025-03-18
- Version: v1.0.0

| Version | Date       | History                 |
| ------- | ---------- | ------------------------|
| v1.0.0  | 2025-03-18 | Initial creation        |


<div style="page-break-after: always;"></div>

# Table of Contents
- [APIs](#api-list)
    - [0. constructor](#0-constructor)
    - [1. isExistWallet](#1-isexistwallet)
    - [2. createWallet](#2-createwallet)
    - [3. deleteWallet](#3-deletewallet)
    - [4. createWalletTokenSeed](#4-createwallettokenseed)
    - [5. createNonceForWalletToken](#5-createnonceforwallettoken)
    - [6. bindUser](#6-binduser)
    - [7. unbindUser](#7-unbinduser)
    - [8. registerLock](#8-registerlock)
    - [9. authenticateLock](#9-authenticatelock)
    - [10. createHolderDIDDoc](#10-createholderdiddoc)
    - [11. createSignedDIDDoc](#11-createsigneddiddoc)
    - [12. getDIDDocument](#12-getdiddocument)
    - [13. generateKeyPair](#13-generatekeypair)
    - [14. isLock](#14-islock)
    - [15. getSignedWalletInfo](#15-getsignedwalletinfo)
    - [16. requestRegisterUser](#16-requestregisteruser)
    - [17. getSignedDIDAuth](#17-getsigneddidauth)
    - [18. requestIssueVc](#18-requestissuevc)
    - [19. requestRevokeVc](#19-requestrevokevc)
    - [20. getAllCredentials](#20-getallcredentials)
    - [21. getCredentials](#21-getcredentials)
    - [22. deleteCredentials](#22-deletecredentials)
    - [23. createEncVp](#23-createencvp)
    - [24. registerBioKey](#24-registerbiokey)
    - [25. authenticateBioKey](#25-authenticatebiokey)
    - [26. addProofsToDocument](#26-addproofstodocument)
    - [27. isSavedBioKey](#27-issavedbiokey)
    - [28. changePin](#28-changepin)
    - [29. changeLock](#29-changelock)

- [Enumerators](#enumerators)
    - [1. WALLET_TOKEN_PURPOSE](#1-wallet_token_purpose)
- [Value Object](#value-object)
    - [1. WalletTokenSeed](#1-wallettokenseed)
    - [2. WalletTokenData](#2-wallettokendata)
    - [3. Provider](#3-provider)
    - [4. SignedDIDDoc](#4-signeddiddoc)
    - [5. SignedWalletInfo](#5-signedwalletinfo)
    - [6. DIDAuth](#6-didauth)
# API List
## 0. constructor

### Description
 `WalletApi constructor`

### Declaration

```kotlin
@JvmStatic
@Throws(WalletCoreException::class)
fun getInstance(context: Context): WalletApi?
```

### Parameters

| Name      | Type   | Description                             | **M/O** | **Note** |
|-----------|--------|----------------------------------|---------|----------|
| context   | Context |                       | M       |          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| WalletApi | WalletApi instance | M       |          |

### Usage

```java
WalletApi walletApi = WalletApi.getInstatnce(context)
```

<br>

## 1. isExistWallet

### Description
 `Checks if DeviceKey Wallet exists.`

### Declaration

```kotlin
fun isExistWallet(): Boolean
```

### Parameters

N/A

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether the wallet exists. | M       |          |

### Usage

```java
boolean exists = walletApi.isExistWallet();
```

<br>

## 2. createWallet

### Description
`Creates a DeviceKey Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createWallet(walletUrl: String, tasUrl: String): Boolean
```

### Parameters

| Name      | Type   | Description                      | **M/O** | **Note** |
|-----------|--------|----------------------------------|---------|----------|
| walletUrl    | String | Wallet URL                          | M       |          |
| tasUrl | String | TAS URL                       | M       |          |


### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether wallet creation was successful. | M       |          |

### Usage

```java
boolean success = walletApi.createWallet();
```

<br>

## 3. deleteWallet

### Description
`Deletes the DeviceKey Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun deleteWallet()
```

### Parameters

Unit

### Returns

N/A

### Usage

```java
walletApi.deleteWallet();
```

<br>

## 4. createWalletTokenSeed

### Description
`Creates a wallet token seed.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createWalletTokenSeed(purpose: WALLET_TOKEN_PURPOSE, pkgName: String, userId: String): WalletTokenSeed
```

### Parameters

| Name      | Type   | Description                             | **M/O** | **Note** |
|-----------|--------|----------------------------------|---------|----------|
| purpose   | WALLET_TOKEN_PURPOSE | Token usage purpose | M       |[WALLET_TOKEN_PURPOSE](#1-wallet_token_purpose)         |
| pkgName   | String | Authorized app Package Name | M       |          |
| userId    | String | User ID | M       |          |

### Returns

| Type            | Description                  | **M/O** | **Note** |
|-----------------|-----------------------|---------|----------|
| WalletTokenSeed | Wallet token seed object | M       |[WalletTokenSeed](#1-wallettokenseed)          |

### Usage

```java
WalletTokenSeed tokenSeed = walletApi.createWalletTokenSeed(purpose, "org.opendid.did.ca", "user_id");
```

<br>

## 5. createNonceForWalletToken

### Description
`Creates a nonce for wallet token generation.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createNonceForWalletToken(apiGateWayUrl: String, walletTokenData: WalletTokenData): String
```

### Parameters

| Name           | Type           | Description                  | **M/O** | **Note** |
|----------------|----------------|-----------------------|---------|----------|
| apiGateWayUrl | String | API GW URL | M       |          |
| walletTokenData | WalletTokenData | Wallet token data | M       |[WalletTokenData](#2-wallettokendata)          |

### Returns

| Type    | Description              | **M/O** | **Note** |
|---------|-------------------|---------|----------|
| String  | Nonce for wallet token generation | M       |          |

### Usage

```java
String nonce = walletApi.createNonceForWalletToken(apiGateWayUrl, walletTokenData);
```

<br>

## 6. bindUser

### Description
`Performs user personalization on the Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun bindUser(hWalletToken: String): Boolean 
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| hWalletToken  | String | Wallet token | M       |          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether personalization was successful. | M       |          |

### Usage

```java
boolean success = walletApi.bindUser("hWalletToken");
```

<br>

## 7. unbindUser

### Description
`Performs user depersonalization.`

### Declaration

```kotlin
@Throws(Exception::class)
fun unbindUser(hWalletToken: String): Boolean
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| hWalletToken  | String | Wallet token | M       |          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether depersonalization was successful. | M       |          |

### Usage

```java
boolean success = walletApi.unbindUser("hWalletToken");
```

<br>

## 8. registerLock

### Description
`Sets the lock state of the Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun registerLock(hWalletToken: String, passCode: String, isLock: Boolean): Boolean
```

### Parameters

| Name         | Type   | Description                        | **M/O** | **Note** |
|--------------|--------|-----------------------------|---------|----------|
| hWalletToken | String | Wallet token | M       |          |
| passCode     | String | Unlock PIN | M       |          |
| isLock       | Boolean | Whether to enable locking | M       |          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether the lock setting was successful. | M       |          |

### Usage

```java
boolean success = walletApi.registerLock("hWalletToken", "123456", true);
```

<br>

## 9. authenticateLock

### Description
`Performs authentication to unlock the Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun authenticateLock(passCode: String)
```

### Parameters

| Name         | Type   | Description                        | **M/O** | **Note** |
|--------------|--------|-----------------------------|---------|----------|
| passCode     | String | Unlock PIN | M       | PIN set during registerLock | 

### Returns

Unit

### Usage

```java
walletApi.authenticateLock("hWalletToken", "123456");
```

<br>

## 10. createHolderDIDDoc

### Description
`Creates a user DID Document.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createHolderDIDDoc(hWalletToken: String): DIDDocument
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| hWalletToken  | String | Wallet token | M       |          |

### Returns

| Type         | Description                  | **M/O** | **Note** |
|--------------|-----------------------|---------|----------|
| DIDDocument  | User DID Document | M       |          |

### Usage

```java
DIDDocument didDoc = walletApi.createHolderDIDDoc("hWalletToken");
```

<br>

## 11. createSignedDIDDoc

### Description
`Creates a signed user DID Document object.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createSignedDIDDoc(ownerDIDDoc: DIDDocument): SignedDidDoc
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| ownerDIDDoc  | DIDDocument | Owner's DID Document object | M       |          |

### Returns

| Type            | Description                  | **M/O** | **Note** |
|-----------------|-----------------------|---------|----------|
| SignedDidDoc | Signed DID Document object | M       |[SignedDIDDoc](#4-signeddiddoc)          |

### Usage

```java
SignedDidDoc signedDidDoc = walletApi.createSignedDIDDoc(ownerDIDDoc);
```

<br>

## 12. getDIDDocument

### Description
`Retrieves a DID Document.`

### Declaration

```kotlin
@Throws(Exception::class)
fun getDIDDocument(type: Int): DIDDocument?
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| type  | Int | 1: deviceKey DID Document, 2: holder DID document | M       |          |

### Returns

| Type         | Description                  | **M/O** | **Note** |
|--------------|-----------------------|---------|----------|
| DIDDocument  | DID Document | M       |          |

### Usage

```java
DIDDocument didDoc = walletApi.getDIDDocument("hWalletToken", 1);
```

<br>

## 13. generateKeyPair

### Description
`Generates a PIN key pair for signing and stores it in the Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun generateKeyPair(hWalletToken: String?, passcode: String?)
```

### Parameters

| Name         | Type   | Description                        | **M/O** | **Note** |
|--------------|--------|-----------------------------|---------|----------|
| hWalletToken | String | Wallet token | M       |          |
| passCode     | String | Signing PIN | M       | PIN for signature key generation | 

### Returns

Unit

### Usage

```java
walletApi.generateKeyPair("hWalletToken", "123456");
```

<br>

## 14. isLock

### Description
`Checks the lock type of the Wallet.`

### Declaration

```kotlin
fun isLock(): Boolean
```

### Parameters
 Void

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns the Wallet lock type. | M       |          |

### Usage

```java
boolean isLocked = walletApi.isLock();
```

<br>

## 15. getSignedWalletInfo

### Description
`Retrieves signed Wallet information.`

### Declaration

```kotlin
@Throws(Exception::class)
fun getSignedWalletInfo(): SignedWalletInfo
```

### Parameters

Void

### Returns

| Type             | Description                    | **M/O** | **Note** |
|------------------|-------------------------|---------|----------|
| SignedWalletInfo | Signed WalletInfo object | M       |[SignedWalletInfo](#5-signedwalletinfo)          |

### Usage

```java
SignedWalletInfo signedInfo = walletApi.getSignedWalletInfo();
```

<br>

## 16. requestRegisterUser

### Description
`Requests user registration.`

### Declaration

```kotlin
@Throws(Exception::class)
fun requestRegisterUser(hWalletToken: String, tasUrl: String, txId: String, serverToken: String, signedDIDDoc: SignedDidDoc): CompletableFuture<String>
```

### Parameters

| Name         | Type           | Description                        | **M/O** | **Note** |
|--------------|----------------|-----------------------------|---------|----------|
| tasUrl | String         | TAS URL | M       |          |
| hWalletToken | String         | Wallet token | M       |          |
| txId     | String       | Transaction code | M       |          |
| serverToken     | String       | Server token | M       |          |
| signedDIDDoc|SignedDidDoc | Signed DID Document object | M       |[SignedDIDDoc](#4-signeddiddoc)          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| String | Returns the result of the user registration protocol execution. | M       |          |

### Usage

```java
String _M132_RequestRegisterUser = walletApi.requestRegisterUser("hWalletToken", "txId", "hServerToken", signedDIDDoc).get();
```

<br>

## 17. getSignedDIDAuth

### Description
`Performs DIDAuth signing.`

### Declaration

```kotlin
@Throws(Exception::class)
fun getSignedDIDAuth(authNonce: String, passcode: String?): DIDAuth
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| authNonce  | String | Auth nonce from profile | M       |          |
|passcode|String | Signing PIN | M       |          |

### Returns

| Type            | Description                  | **M/O** | **Note** |
|-----------------|-----------------------|---------|----------|
| DIDAuth   | Signed DIDAuth object | M       |[DIDAuth](#6-didauth)          |

### Usage

```java
DIDAuth signedDIDAuth = walletApi.getSignedDIDAuth("authNonce", "123456");
```

<br>

## 18. requestIssueVc

### Description
`Requests VC issuance.`

### Declaration

```kotlin
@Throws(Exception::class)
fun requestIssueVc(hWalletToken: String, tasUrl: String, apiGateWayUrl: String, serverToken: String, refId: String, profile: IssueProfile, signedDIDAuth: DIDAuth, txId: String): CompletableFuture<String>
```

### Parameters

| Name        | Type           | Description                        | **M/O** | **Note** |
|-------------|----------------|-----------------------------|---------|----------|
| tasUrl | String         | TAS URL | M       |          |
| hWalletToken | String         | Wallet token | M       |          |
| txId     | String       | Transaction code | M       |          |
| serverToken     | String       | Server token | M       |          |
| refId     | String       | Reference number | M       |          |
| profile|IssueProfile | Issue Profile | M       |[See data model reference]          |
| signedDIDAuth|DIDAuth | Signed DID Document object | M       |[DIDAuth](#6-didauth)         |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| String | VC ID | M       |Returns the ID of the issued VC if successful          |

### Usage

```java
String vcId = walletApi.requestIssueVc("hWalletToken", "txId", "hServerToken", "refId", profile, signedDIDAuth).get();
```

<br>

## 19. requestRevokeVc

### Description
`Requests VC revocation.`

### Declaration

```kotlin
@Throws(Exception::class)
fun requestRevokeVc(hWalletToken: String, tasUrl: String, serverToken: String, txId: String, vcId: String, issuerNonce: String, passcode: String, authType: VERIFY_AUTH_TYPE): CompletableFuture<String>
```

### Parameters

| Name        | Type           | Description                        | **M/O** | **Note** |
|-------------|----------------|-----------------------------|---------|----------|
| hWalletToken | String         | Wallet token | M       |          |
| tasUrl | String         | TAS URL | M       |          |
| txId     | String       | Transaction code | M       |          |
| serverToken     | String       | Server token | M       |          |
| vcId     | String       | VC ID | M       |          |
| issuerNonce|String | Issuer nonce | M       |[See data model reference]          |
| passcode|String | Signing PIN | M       |[DIDAuth](#6-didauth)         |
| authType|VERIFY_AUTH_TYPE | Authentication type for submission | M       |       |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| String | txId | M       |Returns the transaction code if successful          |

### Usage

```java
String result = walletApi.requestRevokeVc("hWalletToken", "hServerToken", "txId", "vcId", "issuerNonce", "123456").get();
```

<br>

## 20. getAllCredentials

### Description
`Retrieves all VCs stored in the Wallet.`

### Declaration

```kotlin
@Throws(Exception::class)
fun getAllCredentials(hWalletToken: String): List<VerifiableCredential>?
```

### Parameters

| Name          | Type   | Description                       | **M/O** | **Note** |
|---------------|--------|----------------------------|---------|----------|
| hWalletToken  | String | Wallet token | M       |          |

### Returns

| Type            | Description                | **M/O** | **Note** |
|-----------------|---------------------|---------|----------|
| List&lt;VerifiableCredential&gt; | VC List object | M       |          |

### Usage

```java
List<VerifiableCredential> vcList = walletApi.getAllCredentials("hWalletToken");
```

<br>

## 21. getCredentials

### Description
`Retrieves specific VCs.`

### Declaration

```kotlin
@Throws(Exception::class)
fun getCredentials(hWalletToken: String, identifiers: List<String>): List<VerifiableCredential>?
```

### Parameters

| Name           | Type   | Description                       | **M/O** | **Note** |
|----------------|--------|----------------------------|---------|----------|
| hWalletToken   | String | Wallet token | M       |          |
| identifiers   | List&lt;String&gt;   | List of VC IDs to retrieve | M       |          |

### Returns

| Type        | Description                | **M/O** | **Note** |
|-------------|---------------------|---------|----------|
| List&lt;VerifiableCredential&gt;  | VC List object | M       |          |

### Usage

```java
List<VerifiableCredential> vcList = walletApi.getCredentials("hWalletToken", List.of("vcId"));
```

<br>

## 22. deleteCredentials

### Description
`Deletes specific VCs.`

### Declaration

```kotlin
@Throws(Exception::class)
fun deleteCredentials(hWalletToken: String, vcId: String)
```

### Parameters

| Name           | Type   | Description                       | **M/O** | **Note** |
|----------------|--------|----------------------------|---------|----------|
| hWalletToken   | String | Wallet token | M       |          |
| vcId   | String   | VC ID to delete | M       |          |

### Returns
N/A

### Usage

```java
walletApi.deleteCredentials("hWalletToken", "vcId");
```

<br>

## 23. createEncVp

### Description
`Creates an encrypted VP.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createEncVp(hWalletToken: String, vcId: String, claimCode: List<String>, reqE2e: ReqE2e, passcode: String, nonce: String, authType: VERIFY_AUTH_TYPE): ReturnEncVP

```

### Parameters

| Name        | Type           | Description                        | **M/O** | **Note** |
|-------------|----------------|-----------------------------|---------|----------|
| hWalletToken | String         | Wallet token | M       |          |
| vcId     | String       | VC ID | M       |          |
| claimCode     | List&lt;String&gt;       | Claim codes to submit | M       |          |
| reqE2e     | ReqE2e       | E2E encryption information | M       |See data model reference       |
|passcode|String | Signing PIN | M       |          |
| nonce|String | nonce | M       |       |
| authType|VERIFY_AUTH_TYPE | Authentication type for submission | M       |       |

### Returns

| Type   | Description              | **M/O** | **Note** |
|--------|-------------------|---------|----------|
| ReturnEncVP  | Encrypted VP object | M       |Contains acce2e object and multibase-encoded encVp value      |

### Usage

```java
EncVP encVp = walletApi.createEncVp("hWalletToken", "vcId", List.of("claim_code"), reqE2e, "123456", "nonce", VERIFY_AUTH_TYPE.PIN);
```

<br>

## 24. registerBioKey

### Description
`Registers a biometric authentication key for signing.`

### Declaration

```kotlin
@Throws(Exception::class)
fun registerBioKey(ctx: Context)
```

### Parameters

| Name         | Type     | Description                        | **M/O** | **Note** |
|--------------|----------|-----------------------------|---------|----------|
| context       | Context   |        | M       |          |

### Returns
N/A

### Usage

```java
walletApi.registerBioKey("hWalletToken", context);
```

<br>

## 25. authenticateBioKey

### Description
`Performs authentication to use the biometric authentication key for signing.`

### Declaration

```kotlin
@Throws(Exception::class)
fun authenticateBioKey(fragment: Fragment, ctx: Context)
```

### Parameters

| Name         | Type     | Description                        | **M/O** | **Note** |
|--------------|----------|-----------------------------|---------|----------|
| fragment       | Fragment   |       | M       |          |
| context       | Context   |        | M       |          |

### Returns

N/A

### Usage

```java
walletApi.authenticateBioKey(fragment.this, context);
```

<br>

## 26. addProofsToDocument

### Description
`Adds a Proof object to an object that needs signing.`

### Declaration

```kotlin
@Throws(WalletException::class, UtilityException::class, WalletCoreException::class)
fun addProofsToDocument(document: ProofContainer, keyIds: List<String>, did: String, type: Int, passcode: String?, isDIDAuth: Boolean): ProofContainer
```

### Parameters

| Name         | Type         | Description                        | **M/O** | **Note** |
|--------------|--------------|-----------------------------|---------|----------|
| document     | ProofContainer     | Document object that inherits from Proof | M       |          |
| keyIds       | List&lt;String&gt;       | List of Key IDs to sign with | M       |          |
| did     | String     | Target DID for signing | M       |          |
| type       | Int       | 1: deviceKey DID Document, 2: holder DID document | M       |          |
| passcode     | String     | Signing PIN | O       | For PIN key signing |
| isDIDAuth       | Boolean       | True if it's a DIDAuth object, false otherwise | M       |          |

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| ProofContainer | Original object including Proof object | M       |          |

### Usage

```java
DIDDocument signedDIDDoc = (DIDDocument) walletApi.addProofsToDocument(didDocument, List.of("PIN"), "DID", 2, "123456", false);
```

<br>


## 27. isSavedBioKey

### Description
`Checks if there is a saved biometric authentication key.`

### Declaration

```kotlin
@Throws(Exception::class)
fun isSavedKey(id: String): Boolean
```

### Parameters

Unit

### Returns

| Type    | Description                | **M/O** | **Note** |
|---------|---------------------|---------|----------|
| Boolean | Returns whether a biometric authentication key exists. | M       |          |

### Usage

```java
boolean hasBioKey = walletApi.isSavedBioKey();
```

<br>

## 28. changePin

### Description
`Changes the signing PIN`

### Declaration

```kotlin
@Throws(Exception::class)
fun changePin(keyId: String, oldPin: String, newPin: String)
```

### Parameters

| Name   | Type   | Description   | **M/O** | **Note** |
| ------ | ------ | ------------- | ------- | -------- |
| keyId     | String | Signing key ID | M       |          |
| oldPIN | String | Current PIN | M       |          |
| newPIN | String | New PIN | M       |          |

### Returns


### Usage

```java
String oldPin = "123456";
String newPin = "654321";
walletApi.changePin(Constants.KEY_ID_PIN, oldPin, newPin);
```

<br>

## 29. changeLock

### Description
`Changes the Unlock PIN`

### Declaration

```kotlin
@Throws(Exception::class)
fun changeLock(oldPin: String, newPin: String)
```

### Parameters

| Name   | Type   | Description   | **M/O** | **Note** |
| ------ | ------ | ------------- | ------- | -------- |
| oldPIN | String | Current PIN | M       |          |
| newPIN | String | New PIN | M       |          |

### Returns


### Usage

```java
String oldPin = "123456";
String newPin = "654321";
walletApi.changeLock(oldPin, newPin);
```

<br>

# Enumerators
## 1. WALLET_TOKEN_PURPOSE

### Description

`WalletToken purpose`

### Declaration

```kotlin
class WalletTokenPurpose {
    enum class WALLET_TOKEN_PURPOSE(val intValue: Int) : IntEnum {
        PERSONALIZE(1),
        DEPERSONALIZE(2),
        PERSONALIZE_AND_CONFIGLOCK(3),
        CONFIGLOCK(4),
        CREATE_DID(5),
        UPDATE_DID(6),
        RESTORE_DID(7),
        ISSUE_VC(8),
        REMOVE_VC(9),
        PRESENT_VP(10),
        LIST_VC(11),
        DETAIL_VC(12),
        CREATE_DID_AND_ISSUE_VC(13),
        LIST_VC_AND_PRESENT_VP(14);
..
```
<br>

# Value Object

## 1. WalletTokenSeed

### Description

`Data that the authorized app delivers to the wallet when requesting wallet token generation`

### Declaration

```kotlin
data class WalletTokenSeed(
    var purpose: WalletTokenPurpose.WALLET_TOKEN_PURPOSE? = null,
    var pkgName: String? = null,
    var nonce: String? = null,
    var validUntil: String? = null,
    var userId: String? = null
) {
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| purpose | WALLET_TOKEN_PURPOSE   | Token usage purpose | M       |[WALLET_TOKEN_PURPOSE](#1-wallet_token_purpose)|
| pkgName   | String | Authorized app Package Name | M       |          |
| nonce    | String | Wallet nonce | M       |          |
| validUntil    | String | Token expiration date/time | M       |          |
| userId    | String | User ID | M       |          |
<br>

## 2. WalletTokenData

### Description

`Data that the wallet creates and delivers to the authorized app when requesting wallet token generation`

### Declaration

```kotlin
data class WalletTokenData(
    var seed: WalletTokenSeed? = null,
    var sha256_pii: String? = null,
    var provider: Provider? = null,
    var nonce: String? = null,
    var proof: Proof? = null
) {
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| seed | WalletTokenSeed   | WalletToken Seed | M       |[WalletTokenSeed](#1-wallettokenseed)|
| sha256_pii   | String | Hash value of user PII | M       |          |
| provider    | Provider | Wallet provider information | M       | [Provider](#3-provider)         |
| nonce    | String | Provider nonce | M       |          |
| proof    | Proof | Provider proof | M       |          |
<br>

## 3. Provider

### Description

`Provider information`

### Declaration

```kotlin
data class Provider(
    var did: String? = null,
    var certVcRef: String? = null
    )
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| did    | String | Provider DID | M       |          |
| certVcRef    | String | Provider registration certificate VC URL | M       |          |
<br>

## 4. SignedDIDDoc

### Description

`Data for the document that the wallet signs the holder's DID Document to request registration to the controller`

### Declaration

```kotlin
data class SignedDidDoc(
    var ownerDidDoc: String? = null,
    var wallet: Wallet? = null,
    var nonce: String? = null,
    var _proof: Proof? = null,
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| ownerDidDoc    | String | Multibase-encoded value of ownerDidDoc | M       |          |
| wallet    | Wallet | Object consisting of wallet id and wallet DID | M       |          |
| nonce    | String | Wallet nonce | M       |          |
| proof    | Proof | Wallet proof | M       |          |
<br>

## 5. SignedWalletInfo

### Description

`Signed wallet info data`

### Declaration

```kotlin
data class SignedWalletInfo(
    var wallet: Wallet? = null,
    var nonce: String? = null,
    var _proof: Proof? = null,
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| wallet    | Wallet | Object consisting of wallet id and wallet DID | M       |          |
| nonce    | String | Wallet nonce | M       |          |
| proof    | Proof | Wallet proof | M       |          |
<br>

## 6. DIDAuth

### Description

`DID Auth data`

### Declaration

```kotlin
data class DIDAuth(
    var did: String? = null,
    var authNonce: String? = null,
    var _proof: Proof? = null,
..
```

### Property

| Name          | Type            | Description                | **M/O** | **Note**               |
|---------------|-----------------|----------------------------|---------|------------------------|
| did    | String | DID of the authentication subject | M       |          |
| authNonce    | String | Nonce for DID Auth | M       |          |
| proof    | Proof | Authentication proof | M       |          |
<br>
