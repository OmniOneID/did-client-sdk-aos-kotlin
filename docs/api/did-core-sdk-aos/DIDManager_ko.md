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

Android DIDManager Core SDK API
==

- 주제: DIDManager
- 작성: Sangjun Kim
- 일자: 2025-03-13
- 버전: v1.0.0

| 버전   | 일자       | 변경 내용                 |
| ------ | ---------- | -------------------------|
| v1.0.0 | 2025-03-13 | 초기 작성                 |


<div style="page-break-after: always;"></div>

# 목차
- [APIs](#api-목록)
    - [1. constructor](#1-constructor)
    - [2. genDID](#2-isanykeysaved)
    - [3. isSaved](#3-issaved)
    - [4. createDocument](#4-createdocument)
    - [5. getDocument](#5-getdocument)
    - [6. replaceDocument](#6-replacedocument)
    - [7. saveDocument](#7-savedocument)
    - [8. deleteDocument](#8-deletedocument)
    - [9. addVerificationMethod](#9-addverificationmethod)
    - [10. removeVerificationMethod](#10-removeverificationmethod)
    - [11. addService](#11-addservice)
    - [12. removeService](#12-removeservice)
    - [13. resetChanges](#13-resetchanges)
- [Enumerators](#enumerators)
    - [1. DID_METHOD_TYPE](#1-did_method_type)
    - [2. DID_KEY_TYPE](#2-did_key_type)
- [Value Object](#value-object)
    - [1. DIDDocument](#1-diddocument)
    - [2. DIDKeyInfo](#2-didkeyinfo)
    - [3. KeyInfo](#3-keyinfo)
    - [4. Service](#4-service)

# API 목록
## 1. Constructor

### Description
`DIDManager costructor`

### Declaration

```kotlin
constructor(fileName: String, context: Context)
```


### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| fileName    | string    | 파일명 |M|DIDManager에서 저장할 월렛의 파일명|
| context    | Context | context |M|  |

### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DIDManager  | DIDManager 객체 |M| |


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);
```

<br>

## 2. genDID

### Description
`methodName을 포함한 랜덤한 DID 문자열을 반환한다.`

### Declaration

```kotlin
@JvmStatic
fun genDID(methodName: String): String
```

### Parameters

| Name      | Type   | Description                | **M/O** | **Note**            |
|-----------|--------|----------------------------|---------|---------------------|
| methodName | String | 사용할 method name          | M       |                     |

### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| String | DID 문자열                   | M       | 다음과 같은 형식의 문자열을 반환하다. <br>`did:omn:3kH8HncYkmRTkLxxipTP9PB3jSXB` |



### Usage
```java
String did = DIDManager.genDID("MyDID");
```

<br>

## 3. isSaved

### Description
`월렛에 저장된 DID 문서의 존재 여부를 반환한다.`

### Declaration

```kotlin
fun isSaved(): Boolean
```

### Parameters

n/a

### Returns

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| Boolean  | 저장된 DID 문서 유무 |M| |



### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this)

if (didManager.isSaved()) {
  DIDDocument didDocument = didManager.getDocument();
}
```

<br>

## 4. createDocument

### Description
`"임시 DIDDocument 객체"를 생성하여 내부 변수로 관리한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun createDocument(did: String, keyInfos: List<DIDKeyInfo>, controller: String, service: List<Service>?)
```


### Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| did    | String    | 사용자 DID |M| DID 생성은 [genDID](#2-gendid)를 참고 |
| keyInfos    | List&lt;DIDKeyInfo&gt; | DID 문서에 등록할 공개키 정보 객체의 배열 |    M    | [DIDKeyInfo](#2-didkeyinfo) |
| controller    | String    | DID 문서에 controller로 등록할 DID. <br>null이면, `did` 항목을 사용한다. |    M    | |
| service    | List&lt;Service&gt;    | DID 문서에 명시할 서비스 정보 객체  |    M    | [Service](#4-service) |

### Returns
Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

didManager.createDocument(did, didKeyInfos, did, null);
```

<br>

## 5. getDocument

### Description
`"임시 DIDDocument 객체"를 리턴하며 null이면, 저장된 DID 문서를 리턴한다. `

### Declaration

```kotlin
@Throws(Exception::class)
fun getDocument(): DIDDocument 
```

### Parameters

n/a

### Returns


| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DIDDocument | DID 문서 |M|[DIDDocument](#1-diddocument) |


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this)

if (didManager.isSaved()) {
  DIDDocument didDocument = didManager.getDocument();
}
```

<br>

## 6. replaceDocument

### Description
`임시 DIDDocument 객체"를 입력받은 객체로 대치한다.`

### Declaration

```kotlin
fun replaceDocument(didDocument: DIDDocument, needUpdate: Boolean)
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didDocument | DIDDocument | 대체할 DID 문서 객체 |    M    | [DIDDocument](#1-diddocument) |
| needUpdate | Boolean | DID 문서 객체의 updated 속성을 현재 시간으로 업데이트 할 것인지 여부 | M | 대체할 DID 문서 객체가 proof가 추가되지 않은 상태라면 필요에 따라 true를 사용할 수 있다. <br>단, proof가 추가 된 상태라면, 서명 원문 보존을 위해서 false를 사용해야 한다. |


### Returns
Unit

### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

DIDDocument newDIDDocument = new DIDDocument();
didManager.replaceDocument(newDIDDocument, false);
```

<br>

## 7. saveDocument

### Description
`“임시 DIDDocument 객체”를 월렛 파일에 저장한 후에 초기화 한다.
이미 저장된 파일을 대상으로 변경사항이 없는 상태, 즉, “임시 DIDDocument 객체”가 null인 상태에서 호출한다면 아무런 동작을 하지 않는다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun saveDocument()
```

### Parameters

n/a


### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

DIDDocument newDIDDocument = new DIDDocument();
didManager.replaceDocument(newDIDDocument, false);

didManager.saveDocument();
```

<br>

## 8. deleteDocument

### Description
`저장된 월렛 파일을 삭제한다. 파일 삭제후, “임시 DIDDocument 객체”를 null로 초기화 한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun deleteDocument()
```

### Parameters

n/a


### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

didManager.deleteDocument();
```

<br>

## 9. addVerificationMethod

### Description
`“임시 DIDDocument 객체”에 공개키 정보를 추가한다.
주로 저장된 DID 문서에 새로운 공개키 정보를 추가하는 경우에 사용한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun addVerificationMethod(keyInfo: DIDKeyInfo)
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyInfo    | DIDKeyInfo    | 키아이디 리스트 |M|  [DIDKeyInfo](#2-didkeyinfo) |

### Returns

Unit


### Usage
```java
// KeyManager 추가할 키쌍생성
KeyManager<DetailKeyInfo> keyManager = new KeyManager<>("MyWallet", this);

WalletKeyGenRequest keyGenInfo = new WalletKeyGenRequest(
                    "PIN",
                    AlgorithmType.ALGORITHM_TYPE.SECP256R1,
                    StorageOption.STORAGE_OPTION.WALLET,
                    new KeyGenWalletMethodType("password".getBytes())
                    );
keyManager.generateKey(keyGenInfo);
List<KeyInfo> keyInfos = keyManager.getKeyInfos(List.of("PIN"));


DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

DIDKeyInfo didKeyInfo = new DIDKeyInfo(
                        keyInfos.get(0),
                        DIDMethodType.DID_METHOD_TYPE.assertionMethod,
                        did
                        );
didManager.addVerificationMethod(didKeyInfo);
```

<br>

## 10. removeVerificationMethod

### Description
`“임시 DIDDocument 객체”에 공개키 정보를 삭제한다.
주로 저장된 DID 문서에 등록된 공개키 정보를 삭제하는 경우에 사용한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun removeVerificationMethod(keyId: String)
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String    | DID 문서에서 삭제할 공개키 정보의 ID |M| |


### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

didManager.removeVerificationMethod("PIN");
```

<br>

## 11. addService

### Description
`“임시 DIDDocument 객체”에 서비스 정보를 추가한다.
주로 저장된 DID 문서에 등록된 서비스 정보를 추가하는 경우에 사용한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun addService(service: Service)
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| service    | Service    |  DID 문서에 명시할 서비스 정보 객체 |M|[Service](#4-service)|


### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

Service service = new Service("serviceId", DIDServiceType.DID_SERVICE_TYPE.credentialRegistry, List.of("http://serviceEndpoint"));
didManager.addService(service);
```
<br>

## 12. removeService

### Description
`“임시 DIDDocument 객체”에서 서비스 정보를 삭제한다.
주로 저장된 DID 문서에 등록된 서비스 정보를 삭제하는 경우에 사용한다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun removeService(serviceId: String)
```

### Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| serviceId    | String    | DID 문서에서 삭제할 서비스 ID |M| |


### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

didManager.removeService("serviceId");
```
<br>

## 13. resetChanges

### Description
`변경사항을 초기화하기 위해 “임시 DIDDocument 객체”를 null로 초기화 한다.
저장된 DID 문서 파일이 없는 경우에는 에러가 발생한다. 즉, 저장된 DID 문서 파일이 있는 경우에만 사용 가능하다.`

### Declaration

```kotlin
@Throws(Exception::class)
fun resetChanges()
```

### Parameters
n/a

### Returns

Unit


### Usage
```java
DIDManager<DIDDocument> didManager = new DIDManager<>("MyWallet", this);

didManager.resetChanges());
```
<br>

# Enumerators
## 1. DID_METHOD_TYPE

### Description

`DID 문서에 등록되는 키가 어떤 용도인지 명시하는 타입.`

### Declaration

```kotlin
enum class DID_METHOD_TYPE(val intValue: Int) {
    assertionMethod(1),
    authentication(1 shl 1),
    keyAgreement(1 shl 2),
    capabilityInvocation(1 shl 3),
    capabilityDelegation(1 shl 4);
}
```
<br>

## 2. DID_KEY_TYPE

### Description

`서명키 타입`

### Declaration

```kotlin
enum class DID_KEY_TYPE(private val value: String) {
    rsaVerificationKey2018("RsaVerificationKey2018"),
    secp256k1VerificationKey2018("Secp256k1VerificationKey2018"),
    secp256r1VerificationKey2018("Secp256r1VerificationKey2018");
}
```
<br>

# Value Object

## 1. DIDDocument

### Description

`DID 문서 객체 (DataModel SDK 제공)`
[Link]

### Declaration

```kotlin
class DIDDocument @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String> = listOf("https://www.w3.org/ns/did/v1"),

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("controller")
    @Expose
    var controller: String? = null,

    @SerializedName("verificationMethod")
    @Expose
    var verificationMethod: List<VerificationMethod>? = null,

    @SerializedName("assertionMethod")
    @Expose
    var assertionMethod: List<String>? = null,

    @SerializedName("authentication")
    @Expose
    var authentication: List<String>? = null,

    @SerializedName("keyAgreement")
    @Expose
    var keyAgreement: List<String>? = null,

    @SerializedName("capabilityInvocation")
    @Expose
    var capabilityInvocation: List<String>? = null,

    @SerializedName("capabilityDelegation")
    @Expose
    var capabilityDelegation: List<String>? = null,

    @SerializedName("service")
    @Expose
    var service: List<Service>? = null,

    @SerializedName("created")
    @Expose
    var created: String? = null,

    @SerializedName("updated")
    @Expose
    var updated: String? = null,

    @SerializedName("versionId")
    @Expose
    var versionId: String = "1",

    @SerializedName("deactivated")
    @Expose
    var deactivated: Boolean = false,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
)
```

<br>

## 2. DIDKeyInfo

### Description

`DID Doc에 등록할 공개키 정보 객체.`

### Declaration

```kotlin
data class DIDKeyInfo(
    var keyInfo: KeyInfo,
    var methodType: List<DIDMethodType.DID_METHOD_TYPE>,
    var controller: String
) 
```

### Property

| Name          | Type               | Description                      | **M/O** | **Note**                    |
|---------------|--------------------|----------------------------------|---------|-----------------------------|
| keyInfo       | KeyInfo            | KeyManager가 반환한 공개키 정보 객체   |    M    |                             |
| methodType    | DID_METHOD_TYPE      | DID 문서에 등록되는 공개키가 어떤 용도인지 명시하는 타입 |  M  |                     |
| controller    | String           | DID 문서에 등록되는 공개키의 controller로 등록할 DID. nil이면, DID 문서의 id를 controller로 등록한다. |    M    |                     |

<br>

## 3. KeyInfo

### Description

`KeyManager에서 조회한 키 정보 객체.`[Link]

### Declaration

```kotlin
open class KeyInfo : Meta {
    @SerializedName("authType")
    @Expose
    var authType: AuthType.AUTH_TYPE? = null

    @SerializedName("algorithm")
    @Expose
    var algorithm: AlgorithmType.ALGORITHM_TYPE? = null

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null

    @SerializedName("accessMethod")
    @Expose
    var accessMethod: KeyAccessMethod.KEY_ACCESS_METHOD? = null
}
```

<br>

## 4. Service

### Description

`DID 문서의 service 객체.(DataModel SDK 제공)` [Link]

### Declaration

```kotlin
data class Service(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: DIDServiceType.DID_SERVICE_TYPE? = null,

    @SerializedName("serviceEndpoint")
    @Expose
    var serviceEndpoint: List<String>? = null
)
```
