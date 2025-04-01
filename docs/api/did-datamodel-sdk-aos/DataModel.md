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

Android DataModel SDK
==

- Topic: DataModel
- Author: Sangjun Kim
- Date: 2025-03-14
- Version: v1.0.0

| Version | Date       | Changes                    |
| ------- | ---------- | -------------------------- |
| v1.0.0  | 2025-03-14 | Initial version            |

<div style="page-break-after: always;"></div>


# Contents
- [CoreVo](#corevo)
    - [1. DIDDocument](#1-diddocument)
        - [1.1. VerificationMethod](#11-verificationmethod)
        - [1.2. Service](#12-service)
    - [2. VerifiableCredential](#2-verifiablecredential)
        - [2.1. Issuer](#21-issuer)
        - [2.2. Evidence](#22-evidence)
        - [2.3. CredentialSchema](#23-credentialschema)
        - [2.4. CredentialSubject](#24-credentialsubject)
        - [2.5. Claim](#25-claim)
        - [2.6. Internationalization](#26-internationalization)
    - [3. VerifiablePresentation](#3-verifiablepresentation)
    - [4. Proof](#4-proof)
        - [4.1 VCProof](#41-vcproof)
    - [5. Profile](#5-profile)
        - [5.1. IssueProfile](#51-issueprofile)
            - [5.1.1. Profile](#511-profile)
                - [5.1.1.1. CredentialSchema](#5111-credentialschema)
                - [5.1.1.2. Process](#5112-process)
        - [5.2 VerifyProfile](#52-verifyprofile)
            - [5.2.1. Profile](#521-profile)
                - [5.2.1.1. ProfileFilter](#5211-profilefilter)
                    - [5.2.1.1.1. CredentialSchema](#52111-credentialschema)
                - [5.2.1.2. Process](#5212-process)
        - [5.3. LogoImage](#53-logoimage)
        - [5.4. ProviderDetail](#54-providerdetail)
        - [5.5. ReqE2e](#55-reqe2e)
    - [6. VCSchema](#6-vcschema)
        - [6.1. VCMetadata](#61-vcmetadata)
        - [6.2. CredentialSubject](#62-credentialsubject)
            - [6.2.1. Claim](#621-claim)
                - [6.2.1.1. Namespace](#6211-namespace)
                - [6.2.1.2. ClaimDef](#6212-claimdef)
- [SeviceVo](#servicevo)
    - [1. Protocol](#1-protocol)
        - [1.1. BaseRequest](#11-baserequest)
            - [1.1.1. P131RequestVo](#111-p131requestvo)
            - [1.1.2. P132RequestVo](#112-p132requestvo)
            - [1.1.3. P210RequestVo](#113-p210requestvo)
            - [1.1.4. P310RequestVo](#114-p310requestvo)
        - [1.2. BaseResponse](#12-baseresponse)
            - [1.2.1. P131ResponseVo](#121-p131responsevo)
            - [1.2.2. P132ResponseVo](#122-p132responsevo)
            - [1.2.3. P210ResponseVo](#123-p210responsevo)
            - [1.2.4. P310ResponseVo](#124-p310responsevo)
    - [2. Token](#2-token)
        - [2.1. ServerTokenSeed](#21-servertokenseed)
            - [2.1.1. AttestedAppInfo](#211-attestedappinfo)
                - [2.1.1.1. provider](#2111-provider)
            - [2.1.2. SignedWalletInfo](#212-signedwalletinfo)
                - [2.1.2.1. Wallet](#2121-wallet)
        - [2.2. ServerTokenData](#22-servertokendata)
        - [2.3. WalletTokenSeed](#23-wallettokenseed)
        - [2.4. WalletTokenData](#24-wallettokendata)
    - [3. SecurityChannel](#3-securitychannel)
        - [3.1. ReqEcdh](#31-reqecdh)
        - [3.2. AccEcdh](#32-accecdh)
        - [3.3. AccE2e](#33-acce2e)
        - [3.4. E2e](#34-e2e)
        - [3.5. DIDAuth](#35-didauth)
    - [4. DIDDoc](#4-diddoc)
        - [4.1. DidDocVo](#41-diddocvo)
        - [4.2. AttestedDidDoc](#42-attesteddiddoc)
        - [4.3. SignedDidDoc](#43-signeddiddoc)
    - [5. Offer](#5-offer)
        - [5.1. IssueOfferPayload](#51-issueofferpayload)
        - [5.2. VerifyOfferPayload](#52-verifyofferpayload)
    - [6. Issue VC](#6-issue-vc)
        - [6.1. ReqVC](#61-reqvc)
            - [6.1.1. Profile](#611-profile)
        - [6.2. VCPlanList](#62-vcplanlist)
            - [6.2.1. VCPlan](#621-vcplan)
                - [6.2.1.1. Option](#6211-option)
- [Enumerators](#enumerators)
    - [1. DID_KEY_TYPE](#1-did_key_type)
    - [2. DID_SERVICE_TYPE](#2-did_service_type)
    - [3. PROOF_PURPOSE](#3-proof_purpose)
    - [4. PROOF_TYPE](#4-proof_type)
    - [5. AUTH_TYPE](#5-auth_type)
    - [6. EVIDENCE_TYPE](#6-evidence_type)
    - [7. PRESENCE](#7-presence)
    - [8. PROFILE_TYPE](#8-profile_type)
    - [9. LOGO_IMAGE_TYPE](#9-logo_image_type)
    - [10. CLAIM_TYPE](#10-claim_type)
    - [11. CLAIM_FORMAT](#11-claim_format)
    - [12. LOCATION](#12-location)
    - [13. SYMMETRIC_PADDING_TYPE](#13-symmetric_padding_type)
    - [14. SYMMETRIC_CIPHER_TYPE](#14-symmetric_cipher_type)
    - [15. ALGORITHM_TYPE](#15-algorithm_type)
    - [16. CREDENTIAL_SCHEMA_TYPE](#16-credential_schema_type)
    - [17. ELLIPTIC_CURVE_TYPE](#17-elliptic_curve_type)
    - [18. VERIFY_AUTH_TYPE](#18-verify_auth_type)
    - [19. ROLE_TYPE](#19-role_type)
    - [20. SERVER_TOKEN_PURPOSE](#20-server_token_purpose)
    - [21. WALLET_TOKEN_PURPOSE](#21-wallet_token_purpose)
- [Apis](#apis)
    - [1. deserialize](#1-deserialize)
    - [2. convertFrom](#2-convertfrom)
    - [3. convertTo](#3-convertto)

<br>

# CoreVo

## 1. DIDDocument

### Description

`Document for Decentralized IDs`

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
) : BaseObject(), ProofContainer
```

### Property

| Name                 | Type                 | Description                            | **M/O** | **Note**                    |
|----------------------|----------------------|----------------------------------------|---------|-----------------------------|
| context              | List\<String>        | JSON-LD context                        |    M    |                             | 
| id                   | String               | DID owner's did                        |    M    |                             | 
| controller           | String               | DID controller's did                   |    M    |                             | 
| verificationMethod   | List\<VerificationMethod> | List of DID keys containing public keys |    M    | [VerificationMethod](#11-verificationmethod) | 
| assertionMethod      | List\<String>        | List of assertion key names            |    O    |                             | 
| authentication       | List\<String>        | List of authentication key names       |    O    |                             | 
| keyAgreement         | List\<String>        | List of key agreement key names        |    O    |                             | 
| capabilityInvocation | List\<String>        | List of capability invocation key names|    O    |                             | 
| capabilityDelegation | List\<String>        | List of capability delegation key names|    O    |                             | 
| service              | List\<Service>       | List of services                       |    O    | [Service](#12-service)      | 
| created              | String               | Creation time                          |    M    |                             | 
| updated              | String               | Update time                            |    M    |                             | 
| versionId            | String               | DID version id                         |    M    |                             | 
| deactivated          | Boolean                 | True: Deactivated, False: Activated    |    M    |                             | 
| proof                | Proof                | Owner's proof                          |    O    | [Proof](#4-proof)           | 
| proofs               | [Proof]              | List of owner's proofs                 |    O    | [Proof](#4-proof)           | 

<br>

## 1.1. VerificationMethod

### Description

`List of DID keys containing public keys`

### Declaration

```kotlin
data class VerificationMethod(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: DIDKeyType.DID_KEY_TYPE? = null,

    @SerializedName("controller")
    @Expose
    var controller: String? = null,

    @SerializedName("publicKeyMultibase")
    @Expose
    var publicKeyMultibase: String? = null,

    @SerializedName("authType")
    @Expose
    var authType: AuthType.AUTH_TYPE? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null
)
```
### Property

| Name               | Type          | Description                            | **M/O** | **Note**                     |
|--------------------|---------------|----------------------------------------|---------|------------------------------|
| id                 | String        | Key name                               |    M    |                              | 
| type               | DID_KEY_TYPE  | Key type                               |    M    | [DID_KEY_TYPE](#1-did_key_type) | 
| controller         | String        | Key controller's DID                   |    M    |                              | 
| publicKeyMultibase | String        | Public key                             |    M    | Multibase encoded            | 
| authType           | AUTH_TYPE     | Authentication method for key usage    |    M    | [AUTH_TYPE](#5-auth_type)    | 

<br>

## 1.2. Service

### Description

`List of services`

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

### Property

| Name            | Type               | Description                        | **M/O** | **Note**                                 |
|-----------------|--------------------|------------------------------------|---------|------------------------------------------|
| id              | String             | Service id                         |    M    |                                          | 
| type            | DID_SERVICE_TYPE   | Service type                       |    M    | [DID_SERVICE_TYPE](#2-did_service_type)  | 
| serviceEndpoint | List\<String>      | List of URLs to the service        |    M    |                                          | 

<br>

## 2. VerifiableCredential

### Description

`Decentralized electronic certificate, hereafter referred to as VC`

### Declaration

```kotlin
data class VerifiableCredential @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String>? = null,

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: List<String>? = null,

    @SerializedName("issuer")
    @Expose
    var issuer: Issuer? = null,

    @SerializedName("issuanceDate")
    @Expose
    var issuanceDate: String? = null,

    @SerializedName("validFrom")
    @Expose
    var validFrom: String? = null, // UTC date time

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null, // UTC date time

    @SerializedName("encoding")
    @Expose
    var encoding: String? = null,

    @SerializedName("formatVersion")
    @Expose
    var formatVersion: String? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("evidence")
    @Expose
    var evidence: List<Evidence>? = null,

    @SerializedName("credentialSchema")
    @Expose
    var credentialSchema: CredentialSchema? = null,

    @SerializedName("credentialSubject")
    @Expose
    var credentialSubject: CredentialSubject? = null,

    @SerializedName("proof")
    @Expose
    var proof: VCProof? = null
) : BaseObject()
```

### Property

| Name              | Type                  | Description                        | **M/O** | **Note**                                      |
|-------------------|-----------------------|------------------------------------|---------|-----------------------------------------------|
| context           | List\<String>         | JSON-LD context                    |    M    |                                               |
| id                | String                | VC id                              |    M    |                                               |
| type              | List\<String>         | List of VC types                   |    M    |                                               |
| issuer            | Issuer                | Issuer information                 |    M    | [Issuer](#21-issuer)                          |
| issuanceDate      | String                | Issuance date                      |    M    |                                               |
| validFrom         | String                | Start date of VC validity          |    M    |                                               |
| validUntil        | String                | Expiration date of VC              |    M    |                                               |
| encoding          | String                | Encoding type of VC                |    M    | Default (UTF-8)                               |
| formatVersion     | String                | Format version of VC               |    M    |                                               |
| language          | String                | Language code of VC                |    M    |                                               |
| evidence          | List\<Evidence>       | Evidence                           |    M    | [Evidence](#22-evidence)                      |
| credentialSchema  | CredentialSchema      | Credential schema                  |    M    | [CredentialSchema](#23-credentialschema)      |
| credentialSubject | CredentialSubject     | Credential subject                 |    M    | [CredentialSubject](#24-credentialsubject)    |
| proof             | VCProof               | Issuer's proof                     |    M    | [VCProof](#41-vcproof)                        |

<br>

## 2.1 Issuer

### Description

`Issuer information`

### Declaration

```kotlin
data class Issuer(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null
) 
```

## Property

| Name      | Type   | Description                       | **M/O** | **Note**                 |
|-----------|--------|-----------------------------------|---------|--------------------------|
| id        | String | Issuer DID                        |    M    |                          |
| name      | String | Issuer name                       |    O    |                          |

<br>

## 2.2 Evidence

### Description

`Verification of evidence documents`

### Declaration

```kotlin
data class Evidence(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: EvidenceType.EVIDENCE_TYPE = EvidenceType.EVIDENCE_TYPE.documentVerification,

    @SerializedName("verifier")
    @Expose
    var verifier: String? = null,

    @SerializedName("evidenceDocument")
    @Expose
    var evidenceDocument: String? = null,

    @SerializedName("subjectPresence")
    @Expose
    var subjectPresence: Presence.PRESENCE? = null,

    @SerializedName("documentPresence")
    @Expose
    var documentPresence: Presence.PRESENCE? = null,

    @SerializedName("attribute")
    @Expose
    var attribute: HashMap<String, String>? = null
)
```

## Property

| Name             | Type          | Description                      | **M/O** | **Note**                           |
|------------------|---------------|----------------------------------|---------|------------------------------------|
| id              | String        | URL of the evidence information  |    O    |                                    |
| type             | EVIDENCE_TYPE | Type of evidence                 |    M    | [EVIDENCE_TYPE](#6-evidence_type)  |
| verifier         | String        | Evidence verifier                |    M    |                                    |
| evidenceDocument | String        | Name of the evidence document    |    M    |                                    |
| subjectPresence  | PRESENCE      | Type of subject presence         |    M    | [PRESENCE](#7-presence)            |
| documentPresence | PRESENCE      | Type of document presence        |    M    | [PRESENCE](#7-presence)            |

<br>

## 2.3 CredentialSchema

### Description

`Credential schema`

### Declaration

```kotlin
data class CredentialSchema(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: CredentialSchemaType.CREDENTIAL_SCHEMA_TYPE? = null
) 
```

## Property

| Name      | Type                     | Description            | **M/O** | **Note**                                      |
|-----------|--------------------------|------------------------|---------|-----------------------------------------------|
| id        | String                   | VC schema URL          |    M    |                                               |
| type      | CREDENTIAL_SCHEMA_TYPE   | VC schema format type  |    M    | [CREDENTIAL_SCHEMA_TYPE](#16-credential_schema_type) |

<br>

## 2.4 CredentialSubject

### Description

`Credential subject`

### Declaration

```kotlin
data class CredentialSubject(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("claims")
    @Expose
    var claims: List<Claim>? = null
)
```

## Property

| Name      | Type          | Description        | **M/O** | **Note**                  |
|-----------|---------------|--------------------|---------|---------------------------|
| id        | String        | Subject DID        |    M    |                           |
| claims    | List\<Claim>  | List of claims     |    M    | [Claim](#25-claim)        |

<br>

## 2.5 Claim

### Description

`Subject information`

### Declaration

```kotlin
data class Claim(
    @SerializedName("code")
    @Expose
    var code: String? = null,

    @SerializedName("caption")
    @Expose
    var caption: String? = null,

    @SerializedName("value")
    @Expose
    var value: String? = null,

    @SerializedName("type")
    @Expose
    var type: ClaimType.CLAIM_TYPE? = null,

    @SerializedName("format")
    @Expose
    var format: ClaimFormat.CLAIM_FORMAT? = null,

    @SerializedName("hideValue")
    @Expose
    var hideValue: Boolean = false,

    @SerializedName("location")
    @Expose
    var location: Location.LOCATION? = null,

    @SerializedName("digestSRI")
    @Expose
    var digestSRI: String? = null,

    @SerializedName("i18n")
    @Expose
    var i18n: Map<String, Internationalization>? = null
) 
```

## Property

| Name      | Type                              | Description                    | **M/O** | **Note**                                  |
|-----------|-----------------------------------|--------------------------------|---------|-------------------------------------------|
| code      | String                            | Claim code                     |    M    |                                           |
| caption   | String                            | Claim name                     |    M    |                                           |
| value     | String                            | Claim value                    |    M    |                                           |
| type      | CLAIM_TYPE                        | Claim type                     |    M    | [CLAIM_TYPE](#11-claim_type)              |
| format    | CLAIM_FORMAT                      | Claim format                   |    M    | [CLAIM_FORMAT](#12-claim_format)          |
| hideValue | Boolean                              | Hide value                     |    O    | Default (false)                           |
| location  | LOCATION                          | Value location                 |    O    | Default (inline) <br> [LOCATION](#12-location) |
| digestSRI | String                            | Digest Subresource Integrity   |    O    |                                           |
| i18n      | Map\<String, Internationalization> | Internationalization           |    O   | [Internationalization](#26-internationalization) |

<br>

## 2.6 Internationalization

### Description

`Internationalization`

### Declaration

```kotlin
data class Internationalization(
    var caption: String? = null,
    var value: String? = null,
    var digestSRI: String? = null
)
```

## Property

| Name      | Type   | Description                  | **M/O** | **Note**                              |
|-----------|--------|------------------------------|---------|---------------------------------------|
| caption   | String | Claim name                   |    M    |                                       |
| value     | String | Claim value                  |    O    |                                       |
| digestSRI | String | Digest Subresource Integrity |    O    | Hash of claim value                   |

<br>

## 3. VerifiablePresentation

### Description

`List of VCs signed by the subject, hereafter referred to as VP`

### Declaration

```kotlin
data class VerifiablePresentation @JvmOverloads constructor(
    @SerializedName("@context")
    @Expose
    var context: List<String> = listOf("https://www.w3.org/ns/credentials/v2"),

    @SerializedName("id")
    @Expose
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("type")
    @Expose
    var type: List<String> = listOf("VerifiablePresentation"),

    @SerializedName("holder")
    @Expose
    var holder: String? = null,

    @SerializedName("validFrom")
    @Expose
    var validFrom: String? = null, // UTC date time

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null, // UTC date time

    @SerializedName("verifierNonce")
    @Expose
    var verifierNonce: String? = null,

    @SerializedName("verifiableCredential")
    @Expose
    var verifiableCredential: List<VerifiableCredential>? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer
```

### Property

| Name                 | Type                        | Description                      | **M/O** | **Note**                                       |
|----------------------|-----------------------------|----------------------------------|---------|-----------------------------------------------|
| context              | List\<String>               | JSON-LD context                  |    M    |                                               |
| id                   | String                      | VP ID                            |    M    |                                               |
| type                 | List\<String>               | List of VP types                 |    M    |                                               |
| holder               | String                      | Holder's DID                     |    M    |                                               |
| validFrom            | String                      | Start date of VP validity        |    M    |                                               |
| validUntil           | String                      | Expiration date of VP            |    M    |                                               |
| verifierNonce        | String                      | Verifier nonce                   |    M    |                                               |
| verifiableCredential | List\<VerifiableCredential> | List of VCs                      |    M    | [VerifiableCredential](#2-verifiablecredential) |
| proof                | Proof                       | Holder's proof                   |    O    | [Proof](#4-proof)                             | 
| proofs               | List\<Proof>                | List of holder's proofs          |    O    | [Proof](#4-proof)                             | 

<br>

## 4. Proof

### Description

`Holder's proof`

### Declaration

```kotlin
open class Proof @JvmOverloads constructor(
    var created: String? = null, // UTC date time
    var proofPurpose: ProofPurpose.PROOF_PURPOSE? = null,
    var verificationMethod: String? = null,
    var type: ProofType.PROOF_TYPE? = null,
    var proofValue: String? = null
) 
```

### Property

| Name               | Type          | Description                      | **M/O** | **Note**                              |
|--------------------|---------------|----------------------------------|---------|---------------------------------------|
| created            | String        | Creation time                    |    M    |                                       | 
| proofPurpose       | PROOF_PURPOSE | Purpose of the proof             |    M    | [PROOF_PURPOSE](#3-proof_purpose)     | 
| verificationMethod | String        | Key URL used for the proof       |    M    |                                       | 
| type               | PROOF_TYPE    | Type of the proof                |    O    | [PROOF_TYPE](#4-proof_type)           |
| proofValue         | String        | Signature value                  |    O    |                                       |

<br>

## 4.1 VCProof

### Description

`Issuer's proof`

### Declaration

```kotlin
data class VCProof(
    var proofValueList: List<String>? = null
) : Proof()
```

### Property

| Name               | Type          | Description                      | **M/O** | **Note**                              |
|--------------------|---------------|----------------------------------|---------|---------------------------------------|
| created            | String        | Creation time                    |    M    |                                       | 
| proofPurpose       | PROOF_PURPOSE | Purpose of the proof             |    M    | [PROOF_PURPOSE](#3-proof_purpose)     | 
| verificationMethod | String        | Key URL used for the proof       |    M    |                                       | 
| type               | PROOF_TYPE    | Type of the proof                |    O    | [PROOF_TYPE](#4-proof_type)           |
| proofValue         | String        | Signature value                  |    O    |                                       |
| proofValueList     | List\<String> | List of signature values         |    O    |                                       |

<br>

## 5. Profile

## 5.1 IssueProfile

### Description

`Issuer profile`

### Declaration

```kotlin
class IssueProfile(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: ProfileType.PROFILE_TYPE? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("encoding")
    @Expose
    var encoding: String? = null,

    @SerializedName("language")
    @Expose
    var language: String? = null,

    @SerializedName("profile")
    @Expose
    var profile: Profile? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null,
) : ProofContainer
```

### Property

| Name        | Type          | Description           | **M/O** | **Note**                              |
|-------------|---------------|-----------------------|---------|---------------------------------------|
| id          | String        | Profile ID            |    M    |                                       |
| type        | PROFILE_TYPE  | Profile type          |    M    | [PROFILE_TYPE](#8-profile_type)       |
| title       | String        | Profile title         |    M    |                                       |
| description | String        | Profile description   |    O    |                                       |
| logo        | LogoImage     | Logo image            |    O    | [LogoImage](#53-logoimage)            |
| encoding    | String        | Profile encoding type |    M    |                                       |
| language    | String        | Profile language code |    M    |                                       |
| profile     | Profile       | Profile content       |    M    | [Profile](#511-profile)               |
| proof       | Proof         | Holder's proof        |    O    | [Proof](#4-proof)                     |

<br>

## 5.1.1 Profile

### Description

`Profile content`

### Declaration

```kotlin
data class Profile(
    @JvmField
    var issuer: ProviderDetail,
    @JvmField
    var credentialSchema: CredentialSchema,
    @JvmField
    var process: Process
)
```

### Property

| Name             | Type               | Description          | **M/O** | **Note**                              |
|------------------|--------------------|----------------------|---------|---------------------------------------|
| issuer           | ProviderDetail     | Issuer information   |    M    | [ProviderDetail](#54-providerdetail)  |
| credentialSchema | CredentialSchema   | VC schema information|    M    | [CredentialSchema](#5111-credentialschema) |
| process          | Process            | Issuance process     |    M    | [Process](#5112-process)              |

<br>

## 5.1.1.1 CredentialSchema

### Description

`VC schema information`

### Declaration

```kotlin
data class CredentialSchema(
    @JvmField
    var id: String,
    @JvmField
    var type: CredentialSchemaType.CREDENTIAL_SCHEMA_TYPE,
    @JvmField
    var value: String
)
```

### Property

| Name  | Type                     | Description            | **M/O** | **Note**                                           |
|-------|--------------------------|------------------------|---------|----------------------------------------------------|
| id    | String                   | VC schema URL          |    M    |                                                    |
| type  | CREDENTIAL_SCHEMA_TYPE   | VC schema format type  |    M    | [CREDENTIAL_SCHEMA_TYPE](#16-credential_schema_type) |
| value | String                   | VC schema              |    O    | Multibase encoded                                  |

<br>

## 5.1.1.2 Process

### Description

`Issuance process`

### Declaration

```kotlin
data class Process(
    @JvmField
    var endpoints: List<String>,
    @JvmField
    var reqE2e: ReqE2e,
    @JvmField
    var issuerNonce: String
)
```

### Property

| Name        | Type              | Description         | **M/O** | **Note**                            |
|-------------|-------------------|---------------------|---------|-------------------------------------|
| endpoints   | List\<String>     | List of endpoints   |    M    |                                     |
| reqE2e      | ReqE2e            | Request information |    M    | No proof <br> [ReqE2e](#55-reqe2e)  |
| issuerNonce | String            | Issuer nonce        |    M    |                                     |

<br>

## 5.2 VerifyProfile

### Description

`Verification profile`

### Declaration

```kotlin
data class VerifyProfile(
    @SerializedName("id")
    @Expose var id: String?,

    @SerializedName("type")
    @Expose var type: ProfileType.PROFILE_TYPE?,

    @SerializedName("title")
    @Expose var title: String?,

    @SerializedName("description")
    @Expose var description: String?,

    @SerializedName("logo")
    @Expose var logo: LogoImage?,

    @SerializedName("encoding")
    @Expose var encoding: String?,

    @SerializedName("language")
    @Expose var language: String?,

    @SerializedName("profile")
    @Expose var profile: Profile?,

    @SerializedName("proof")
    @Expose var _proof: Proof?,

    @SerializedName("proofs")
    @Expose var _proofs: List<Proof>?
) : ProofContainer
```

### Property

| Name        | Type          | Description           | **M/O** | **Note**                              |
|-------------|---------------|-----------------------|---------|---------------------------------------|
| id          | String        | Profile ID            |    M    |                                       |
| type        | PROFILE_TYPE  | Profile type          |    M    | [PROFILE_TYPE](#8-profile_type)       |
| title       | String        | Profile title         |    M    |                                       |
| description | String        | Profile description   |    O    |                                       |
| logo        | LogoImage     | Logo image            |    O    | [LogoImage](#53-logoimage)            |
| encoding    | String        | Profile encoding type |    M    |                                       |
| language    | String        | Profile language code |    M    |                                       |
| profile     | Profile       | Profile content       |    M    | [Profile](#521-profile)               |
| proof       | Proof         | Holder's proof        |    O    | [Proof](#4-proof)                     |

<br>

## 5.2.1 Profile

### Description

`Profile content`

### Declaration

```kotlin
data class Profile(
    @JvmField
    var verifier: ProviderDetail?,
    @JvmField
    var filter: ProfileFilter?,
    @JvmField
    var process: Process?
)
```

### Property

| Name     | Type           | Description              | **M/O** | **Note**                              |
|----------|----------------|--------------------------|---------|---------------------------------------|
| verifier | ProviderDetail | Verifier information     |    M    | [ProviderDetail](#54-providerdetail)  |
| filter   | ProfileFilter  | Filtering information for Presentation |    M    | [ProfileFilter](#5211-profilefilter)  |
| process  | Process        | VP Presentation process    |    M    | [Process](#5212-process)              |

<br>

## 5.2.1.1 ProfileFilter

### Description

`Filtering information for Presentation`

### Declaration

```kotlin
data class ProfileFilter(
    var credentialSchemas: List<CredentialSchema>?
)
```

### Property

| Name              | Type                      | Description                                | **M/O** | **Note**                                   |
|-------------------|---------------------------|--------------------------------------------|---------|--------------------------------------------|
| credentialSchemas | List\<CredentialSchema>   | Claims and issuers per submitable VC Schema|    M    | [CredentialSchema](#52111-credentialschema)|

<br>

## 5.2.1.1.1 CredentialSchema

### Description

`Claims and issuers per submittable VC Schema`

### Declaration

```kotlin
data class CredentialSchema(
    @JvmField
    var id: String?,
    @JvmField
    var type: CredentialSchemaType.CREDENTIAL_SCHEMA_TYPE?,
    @JvmField
    var varue: String?,
    @JvmField
    var displayClaims: List<String>?,
    @JvmField
    var requiredClaims: List<String>?,
    @JvmField
    var allowedIssuers: List<String>?,
    var presentAll: Boolean
)
```

### Property

| Name           | Type                     | Description                      | **M/O** | **Note**                                      |
|----------------|--------------------------|----------------------------------|---------|-----------------------------------------------|
| id             | String                   | VC schema URL                    |    M    |                                               |
| type           | CREDENTIAL_SCHEMA_TYPE   | VC schema format type            |    M    | [CREDENTIAL_SCHEMA_TYPE](#16-credential_schema_type) |
| value          | String                   | VC schema                        |    O    | Multibase encoded                             |
| displayClaims  | List\<String>            | List of claims to display to user|    O    |                                               |
| requiredClaims | List\<String>            | List of required claims          |    O    |                                               |
| allowedIssuers | List\<String>            | List of allowed issuer DIDs      |    O    |                                               |

<br>

## 5.2.1.2 Process

### Description

`Method for VP submission`

### Declaration

```kotlin
data class Process(
    @JvmField
    var endpoints: List<String>?,
    @JvmField
    var reqE2e: ReqE2e?,
    @JvmField
    var verifierNonce: String?,
    @JvmField
    var authType: VerifyAuthType.VERIFY_AUTH_TYPE?
)
```

### Property

| Name          | Type                  | Description                    | **M/O** | **Note**                              |
|---------------|-----------------------|--------------------------------|---------|---------------------------------------|
| endpoints     | List\<String>         | List of endpoints              |    O    |                                       |
| reqE2e        | ReqE2e                | Request information            |    M    | No proof <br> [ReqE2e](#55-reqe2e)    |
| verifierNonce | String                | Issuer nonce                   |    M    |                                       |
| authType      | VERIFY_AUTH_TYPE      | Authentication method for submission |    O    | [VERIFY_AUTH_TYPE](#18-verify_auth_type) |

<br>

## 5.3. LogoImage

### Description

`Logo image`

### Declaration

```kotlin
data class LogoImage(
    @SerializedName("format")
    @Expose
    var format: LOGO_IMAGE_TYPE? = null,

    @SerializedName("link")
    @Expose
    var link: String? = null,

    @SerializedName("value")
    @Expose
    var value: String? = null
) 
```

### Property

| Name   | Type               | Description         | **M/O** | **Note**                              |
|--------|--------------------|---------------------|---------|---------------------------------------|
| format | LOGO_IMAGE_TYPE    | Image format        |    M    | [LOGO_IMAGE_TYPE](#9-logo_image_type) |
| link   | String             | Logo image URL      |    O    | Multibase encoded                     |
| value  | String             | Image value         |    O    | Multibase encoded                     |

<br>

## 5.4. ProviderDetail

### Description

`Provider details`

### Declaration

```kotlin
data class ProviderDetail(
    @SerializedName("did")
    @Expose
    var did: String? = null,

    @SerializedName("certVcRef")
    @Expose
    var certVcRef: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("ref")
    @Expose
    var ref: String? = null
)
```

### Property

| Name        | Type      | Description                       | **M/O** | **Note**                              |
|-------------|-----------|-----------------------------------|---------|---------------------------------------|
| did         | String    | Provider DID                      |    M    |                                       |
| certVCRef   | String    | Certificate URL                   |    M    |                                       |
| name        | String    | Provider name                     |    M    |                                       |
| description | String    | Provider description              |    O    |                                       |
| logo        | LogoImage | Logo image                        |    O    | [LogoImage](#53-logoimage)            |
| ref         | String    | Reference URL                     |    O    |                                       |

<br>

## 5.5. ReqE2e

### Description

`E2E request data`

### Declaration

```kotlin
data class ReqE2e(
    @SerializedName("nonce")
    @Expose
    var nonce: String? = null,

    @SerializedName("curve")
    @Expose
    var curve: EllipticCurveType.ELLIPTIC_CURVE_TYPE? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("cipher")
    @Expose
    var cipher: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE? = null,

    @SerializedName("padding")
    @Expose
    var padding: SymmetricPaddingType.SYMMETRIC_PADDING_TYPE? = null,

    @SerializedName("proof")
    @Expose
    var proof: Proof? = null
)
```

### Property

| Name      | Type                     | Description                                   | **M/O** | **Note**                                    |
|-----------|--------------------------|-----------------------------------------------|---------|---------------------------------------------|
| nonce     | String                   | Nonce for symmetric key generation            |    M    | Multibase encoded                           |
| curve     | ELLIPTIC_CURVE_TYPE      | Type of elliptic curve                        |    M    | [ELLIPTIC_CURVE_TYPE](#17-elliptic_curve_type) |
| publicKey | String                   | Server public key for encryption              |    M    | Multibase encoded                           |
| cipher    | SYMMETRIC_CIPHER_TYPE    | Type of encryption                            |    M    | [SYMMETRIC_CIPHER_TYPE](#14-symmetric_cipher_type) |
| padding   | SYMMETRIC_PADDING_TYPE   | Type of padding                               |    M    | [SYMMETRIC_PADDING_TYPE](#13-symmetric_padding_type) |
| proof     | Proof                    | Key agreement proof                           |    O    | [Proof](#4-proof)                           |

<br>

## 6. VCSchema

### Description

`VC schema`

### Declaration

```kotlin
data class VCSchema(
    @SerializedName("@id")
    @Expose
    var id: String? = null,

    @SerializedName("@schema")
    @Expose
    var schema: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("metadata")
    @Expose
    var metadata: VCMetadata? = null,

    @SerializedName("credentialSubject")
    @Expose
    var credentialSubject: CredentialSubject? = null
)
```

### Property

| Name              | Type              | Description              | **M/O** | **Note**                                    |
|-------------------|-------------------|--------------------------|---------|---------------------------------------------|
| id                | String            | VC schema URL            |    M    |                                             |
| schema            | String            | VC schema format URL     |    M    |                                             |
| title             | String            | VC schema title          |    M    |                                             |
| description       | String            | VC schema description    |    M    |                                             |
| metadata          | VCMetadata        | VC metadata              |    M    | [VCMetadata](#61-vcmetadata)                |
| credentialSubject | CredentialSubject | Credential subject       |    M    | [CredentialSubject](#62-credentialsubject)  |

<br>

## 6.1. VCMetadata

### Description

`VC Metadata`

### Declaration

```kotlin
data class VCMetadata(
    @JvmField
    var language: String? = null,
    @JvmField
    var formatVersion: String? = null
)
```

### Property

| Name          | Type   | Description         | **M/O** | **Note**                 |
|---------------|--------|---------------------|---------|--------------------------|
| language      | String | Default language of VC |    M    |                          |
| formatVersion | String | VC format version   |    M    |                          |

<br>

## 6.2. CredentialSubject

### Description

`Credential Subject`

### Declaration

```kotlin
data class CredentialSubject(
    @JvmField
    var claims: List<Claim>? = null
)
```

### Property

| Name   | Type              | Description          | **M/O** | **Note**                 |
|--------|-------------------|----------------------|---------|--------------------------|
| claims | List\<Claim>      | Claims by namespace  |    M    | [Claim](#621-claim)      |

<br>

## 6.2.1. Claim

### Description

`Claim`

### Declaration

```kotlin
data class Claim(
    @JvmField
    var namespace: Namespace? = null,
    @JvmField
    var items: List<ClaimDef>? = null
)
```

### Property

| Name      | Type              | Description              | **M/O** | **Note**                              |
|-----------|-------------------|--------------------------|---------|---------------------------------------|
| namespace | Namespace         | Claim namespace          |    M    | [Namespace](#6211-namespace)          |
| items     | List\<ClaimDef>   | List of claim definitions|    M    | [ClaimDef](#6212-claimdef)            |

<br>

## 6.2.1.1. Namespace

### Description

`Claim namespace`

### Declaration

```kotlin
data class Namespace(
    @JvmField
    var id: String? = null,
    @JvmField
    var name: String? = null,
    @JvmField
    var ref: String? = null
)
```

### Property

| Name | Type   | Description                   | **M/O** | **Note**                 |
|------|--------|-------------------------------|---------|--------------------------|
| id   | String | Claim namespace               |    M    |                          |
| name | String | Namespace name                |    M    |                          |
| ref  | String | Namespace information URL     |    O    |                          |

<br>

## 6.2.1.2. ClaimDef

### Description

`Claim definition`

### Declaration

```kotlin
data class ClaimDef(
    @JvmField
    var id: String? = null,
    @JvmField
    var caption: String? = null,
    @JvmField
    var type: ClaimType.CLAIM_TYPE? = null,
    @JvmField
    var format: ClaimFormat.CLAIM_FORMAT? = null,
    @JvmField
    var hideValue: Boolean = false,
    @JvmField
    var location: Location.LOCATION? = null,
    @JvmField
    var required: Boolean = true,
    @JvmField
    var description: String = ""
    @JvmField
    var i18n: Map<String, String>? = null
)
```

### Property

| Name        | Type                   | Description          | **M/O** | **Note**                                   |
|-------------|------------------------|----------------------|---------|--------------------------------------------|
| id          | String                 | Claim ID             |    M    |                                            |
| caption     | String                 | Claim name           |    M    |                                            |
| type        | CLAIM_TYPE             | Claim type           |    M    | [CLAIM_TYPE](#10-claim_type)               |
| format      | CLAIM_FORMAT           | Claim format         |    M    | [CLAIM_FORMAT](#11-claim_format)           |
| hideValue   | Boolean                | Hide value           |    O    | Default(false)                             |
| location    | LOCATION               | Value location       |    O    | Default(inline) <br> [LOCATION](#12-location) |
| required    | Boolean                | Required             |    O    | Default(true)                              |
| description | String                 | Claim description    |    O    | Default("")                                |
| i18n        | Map\<String, String>   | Internationalization |    O    |                                            |

<br>

# ServiceVo

## 1. Protocol

## 1.1. BaseRequest

### Description

`Each request protocol object inherits from this abstract class, which serves as the base class for protocol messages`

### Declaration

```kotlin
open class BaseRequestVo @JvmOverloads constructor(
    @Transient
    open var id: String,
    @Transient
    open var txId: String? = null
)
```

### Property

| Name            | Type           | Description                   | **M/O** | **Note** |
|-----------------|----------------|--------------------------------|---------|----------|
| id            | String | Message ID         |    M    | 
| txId  | String | Transaction ID         |    O    | 

<br>


## 1.1.1. P131RequestVo

### Description

`Request object for register wallet protocol`

### Declaration

```kotlin
data class P131RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var attestedDidDoc: AttestedDidDoc? = null
) : BaseRequestVo(id, txId)
```

### Property

| Name            | Type           | Description                   | **M/O** | **Note** |
|-----------------|----------------|--------------------------------|---------|----------|
| attestedDidDoc  | AttestedDidDoc | Provider Attested DID document          |    M    | [AttestedDidDoc](#42-attesteddiddoc) |

<br>

## 1.1.2. P132RequestVo

### Description

`Request object for register user protocol`

### Declaration

```kotlin
data class P132RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var attestedDidDoc: AttestedDidDoc? = null,
    var reqEcdh: ReqEcdh? = null,
    var seed: ServerTokenSeed? = null,
    var signedDidDoc: SignedDidDoc? = null,
    var serverToken: String? = null,
    var iv: String? = null,
    var kycTxId: String? = null
) : BaseRequestVo(id, txId)
```

### Property

| Name            | Type              | Description                   | **M/O** | **Note** |
|-----------------|-------------------|--------------------------------|---------|----------|
| attestedDIDDoc  | AttestedDidDoc     | Provider Attested DID document          |    M    | [AttestedDidDoc](#42-attesteddiddoc) |
| reqEcdh         | ReqEcdh            | Request Data for ECDH               |    M    | [ReqEcdh](#31-reqecdh) |
| seed            | ServerTokenSeed    | Server token seed              |    M    | [ServerTokenSeed](#21-servertokenseed) |
| signedDidDoc    | SignedDidDoc       | Signed DID document            |    M    | [SignedDidDoc](#43-signeddiddoc) |
| serverToken     | String             | Server token                   |    M    |          |
| iv              | String             | Initialize vector          |    M    |          |
| kycTxId         | String             | KYC transaction ID             |    M    |          |

<br>

## 1.1.3. P210RequestVo

### Description

`Request object for issue VC protocol`

### Declaration

```kotlin
data class P210RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var vcPlanId: String? = null,
    var issuer: String? = null,
    var offerId: String? = null,
    var reqEcdh: ReqEcdh? = null,
    var seed: ServerTokenSeed? = null,
    var serverToken: String? = null,
    var didAuth: DIDAuth? = null,
    var accE2e: AccE2e? = null,
    var encReqVc: String? = null,
    var vcId: String? = null
) : BaseRequestVo(id, txId)
```

### Property

| Name         | Type             | Description                   | **M/O** | **Note** |
|--------------|------------------|--------------------------------|---------|----------|
| vcPlanId     | String            | Verifiable Credential Plan ID  |    M    |          |
| issuer       | String            | Issuer DID       |    M    |          |
| offerId      | String            | Offer ID |    M    |          |
| reqEcdh      | ReqEcdh           | ECDH request data               |    M    | [ReqEcdh](#31-reqecdh) |
| seed         | ServerTokenSeed   | Server token seed              |    M    | [ServerTokenSeed](#21-servertokenseed) |
| serverToken  | String            | Server token                   |    M    |          |
| didAuth      | DIDAuth           | DID Auth data             |    M    | [DIDAuth](#35-didauth) |
| accE2e       | AccE2e            | E2E encryption data     |    M    | [AccE2e](#33-acce2e) |
| encReqVc     | String            | Encrypted Verifiable Credential request data |    M    |          |
| vcId         | String            | Verifiable Credential ID       |    M    |          |

<br>

## 1.1.4. P310RequestVo

### Description

`Request object for submit VP protocol`

### Declaration

```kotlin
data class P310RequestVo @JvmOverloads constructor(
    override var id: String,
    override var txId: String? = null,
    var offerId: String? = null,
    var reqEcdh: ReqEcdh? = null,
    var accE2e: AccE2e? = null,
    var encVp: String? = null
) : BaseRequestVo(id, txId)
```

### Property

| Name     | Type             | Description                      | **M/O** | **Note** |
|----------|------------------|----------------------------------|---------|----------|
| offerId  | String            | Offer ID  |    M    |          |
| reqEcdh  | ReqEcdh           | ECDH request data                 |    M    | [ReqEcdh](#31-reqecdh) |
| accE2e   | AccE2e            | E2E acceptance data       |    M    | [AccE2e](#33-acce2e) |
| encVp    | String            | Encrypted Verifiable Presentation |    M    |          |

<br>

## 1.2. BaseResponse

### Description

`Each response protocol object inherits from this abstract class, which serves as the base class for protocol messages`

### Declaration

```kotlin
open class BaseResponseVo(
    @Transient
    open var txId: String? = null,
    @Transient
    open var code: Int? = null,
    @Transient
    open var message: String? = null
)
```

### Property

| Name            | Type           | Description                   | **M/O** | **Note** |
|-----------------|----------------|--------------------------------|---------|----------|
| txId            | String | Transaction ID         |    M    | 
| code  | Integer | Error code         |    M    | 
| message  | String | Error message         |    M    | 

<br>

## 1.2.1. P131ResponseVo

### Description

`Response object for register wallet protocol`

### Declaration

```kotlin
class P131ResponseVo : BaseResponseVo()
```

### Property
N/A

<br>


## 1.2.2. P132ResponseVo

### Description

`Response object for register user protocol`

### Declaration

```kotlin
data class P132ResponseVo @JvmOverloads constructor(
    override var txId: String? = null,
    override var code: Int? = null,
    override var message: String? = null,
    var iv: String? = null,
    var encStd: String? = null,
    var accEcdh: AccEcdh? = null
) : BaseResponseVo(txId, code, message)
```

### Property

| Name     | Type     | Description                   | **M/O** | **Note** |
|----------|----------|-------------------------------|---------|----------|
| iv       | String   | Initialize vector         |    M    |          |
| encStd   | String   | Encrypted server token data       |    M    |          |
| accEcdh  | AccEcdh  | ECDH acceptance data            |    M    | [AccEcdh](#32-accecdh) |

<br>

## 1.2.3. P210ResponseVo

### Description

`Response object for issue VC protocol`

### Declaration

```kotlin
data class P210ResponseVo @JvmOverloads constructor(
    override var txId: String? = null,
    override var code: Int? = null,
    override var message: String? = null,
    var refId: String? = null,
    var accEcdh: AccEcdh? = null,
    var iv: String? = null,
    var encStd: String? = null,
    var authNonce: String? = null,
    var profile: IssueProfile? = null,
    var e2e: E2e? = null
) : BaseResponseVo(txId, code, message)
```

### Property

| Name       | Type           | Description                      | **M/O** | **Note** |
|------------|----------------|----------------------------------|---------|----------|
| refId      | String          | Reference ID                     |    M    |          |
| accEcdh    | AccEcdh         | ECDH acceptance data            |    M    | [AccEcdh](#32-accecdh) |
| iv         | String          | Initialize vector            |    M    |          |
| encStd     | String          | Encrypted server token data        |    M    |          |
| authNonce  | String          | Auth nonce             |    M    |          |
| profile    | IssueProfile    | Issue profile                   |    M    |[IssueProfile](#51-issueprofile)            |
| e2e        | E2e             | E2E encryption data       |    M    | [E2e](#34-e2e) |

<br>

## 1.2.4. P310ResponseVo

### Description

`Response object for submit VP protocol`

### Declaration

```kotlin
data class P310ResponseVo @JvmOverloads constructor(
    override var txId: String? = null,
    override var code: Int? = null,
    override var message: String? = null,
    var profile: VerifyProfile? = null
) : BaseResponseVo(txId, code, message)
```

### Property

| Name    | Type           | Description                      | **M/O** | **Note** |
|---------|----------------|----------------------------------|---------|----------|
| profile | VerifyProfile   | Verify profile             |    M    | [VerifyProfile](#52-verifyprofile)          |

<br>

## 2. Token
## 2.1. ServerTokenSeed

### Description

`Server token seed`

### Declaration

```kotlin
data class ServerTokenSeed(
    var purpose: ServerTokenPurpose.SERVER_TOKEN_PURPOSE? = null,
    var walletInfo: SignedWalletInfo? = null,
    var caAppInfo: AttestedAppInfo? = null
)
```

### Property

| Name        | Type                                          | Description                   | **M/O** | **Note** |
|-------------|-----------------------------------------------|-------------------------------|---------|----------|
| purpose     | SERVER_TOKEN_PURPOSE       | server token purpose  |    M    | [ServerTokenPurpose](#20-server_token_purpose) |
| walletInfo  | SignedWalletInfo                              | Signed wallet information     |    M    | [SignedWalletInfo](#212-signedwalletinfo) |
| caAppInfo   | AttestedAppInfo                               | Attested app information      |    M    | [AttestedAppInfo](#211-attestedappinfo) |

<br>

## 2.1.1. AttestedAppInfo

### Description

`Attested app information`


### Declaration

```kotlin
data class AttestedAppInfo(
    var appId: String? = null,
    var provider: Provider? = null,
    var nonce: String? = null,
    var proof: Proof? = null
)
```

### Property

| Name     | Type     | Description                   | **M/O** | **Note**                  |
|----------|----------|-------------------------------|---------|---------------------------|
| appId    | String   | Certificated app id       |    M    |                            |
| provider | Provider | Provider information          |    M    | [Provider](#2111-provider)      |
| nonce    | String   | Nonce for attestation|    M    |                            |
| proof    | Proof    | Assertion proof               |    O    | [Proof](#4-proof)         

<br>

## 2.1.1.1. Provider

### Description

`Provider information`

### Declaration

```kotlin
data class Provider(
    var did: String? = null,
    var certVcRef: String? = null
)
```

### Property

| Name       | Type   | Description             | **M/O** | **Note** |
|------------|--------|-------------------------|---------|----------|
| did        | String | Provider DID    |    M    |          |
| certVcRef  | String | Certificate VC URL |    M    |          |

<br>

## 2.1.2. SignedWalletInfo

### Description

`Signed wallet information`

### Declaration

```kotlin
data class SignedWalletInfo(
    @SerializedName("wallet")
    @Expose
    var wallet: Wallet? = null,

    @SerializedName("nonce")
    @Expose
    var nonce: String? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer
```

### Property

| Name       | Type           | Description                | **M/O** | **Note** |
|------------|----------------|----------------------------|---------|----------|
| wallet     | Wallet          | Wallet information         |    M    | [Wallet](#2121-wallet) |
| nonce      | String          | Nonce                |    M    |          |
| proof      | Proof           | Proof               |    O    | [Proof](#4-proof) |
| proofs     | List<Proof>     | List of proofs             |    O    | [Proof](#4-proof) |

<br>

## 2.1.2.1. Wallet

### Description

`Wallet details`

### Declaration

```kotlin
data class Wallet(
    var id: String? = null,
    var did: String? = null
)
```
### Property

| Name       | Type           | Description                | **M/O** | **Note** |
|------------|----------------|----------------------------|---------|----------|
| id         | String          | Wallet ID  |    M    |          |
| did        | String          | Wallet provider DID |    M    |          |

<br>

## 2.2. ServerTokenData

### Description

`Server token data`

### Declaration

```kotlin
data class ServerTokenData(
    var purpose: ServerTokenPurpose.SERVER_TOKEN_PURPOSE? = null,
    var walletId: String? = null,
    var appId: String? = null,
    var validUntil: String? = null,
    var provider: Provider? = null,
    var nonce: String? = null,
    var proof: Proof? = null
)
```

### Property

| Name       | Type                                         | Description                   | **M/O** | **Note** |
|------------|----------------------------------------------|-------------------------------|---------|----------|
| purpose    | SERVER_TOKEN_PURPOSE      | Server token purpose   |    M    | [ServerTokenPurpose](#20-server_token_purpose) |
| walletId   | String                                       | Wallet ID |    M    |          |
| appId      | String                                       | Certificate app ID    |    M    |          |
| validUntil | String                                       | Expiration date of the server token  |    M    |          |
| provider   | Provider                                     | Provider information          |    M    | [Provider](#2111-provider) |
| nonce      | String                                       | Nonce                   |    M    |          |
| proof      | Proof                                        | Proof                  |    O    | [Proof](#4-proof) |

<br>

## 2.3. WalletTokenSeed

### Description

`Wallet token seed`

### Declaration

```kotlin
data class WalletTokenSeed(
    var purpose: WalletTokenPurpose.WALLET_TOKEN_PURPOSE? = null,
    var pkgName: String? = null,
    var nonce: String? = null,
    var validUntil: String? = null,
    var userId: String? = null
)
```

### Property

| Name       | Type                                        | Description                       | **M/O** | **Note** |
|------------|---------------------------------------------|-----------------------------------|---------|----------|
| purpose    | WALLET_TOKEN_PURPOSE     | Wallet token purpose     |    M    | [WalletTokenPurpose](#21-wallet_token_purpose) |
| pkgName    | String                                      | CA package name |    M    |      |
| nonce      | String                                      | Nonce                        |    M    |          |
| validUntil | String                                      | Expiration date of the token      |    M    |          |
| userId     | String                                      | User ID |    M    |          |

<br>


## 2.4. WalletTokenData

### Description

`Wallet token data`

### Declaration

```kotlin
data class WalletTokenData(
    var seed: WalletTokenSeed? = null,
    var sha256_pii: String? = null,
    var provider: Provider? = null,
    var nonce: String? = null,
    var proof: Proof? = null
)
```

### Property

| Name       | Type             | Description                   | **M/O** | **Note** |
|------------|------------------|-------------------------------|---------|----------|
| seed       | WalletTokenSeed   | Wallet token seed     |    M    | [WalletTokenSeed](#23-wallettokenseed) |
| sha256_pii | String            | SHA-256 hash of PII           |    M    |          |
| provider   | Provider          | Provider information          |    M    | [Provider](#2111-provider) |
| nonce      | String            | Nonce                    |    M    |          |
| proof      | Proof             | Proof                  |    O    | [Proof](#4-proof) |

<br>

## 3. SecurityChannel

## 3.1. ReqEcdh

### Description

`ECDH request data`

### Declaration

```kotlin
data class ReqEcdh(
    @SerializedName("client")
    @Expose
    var client: String? = null,

    @SerializedName("clientNonce")
    @Expose
    var clientNonce: String? = null,

    @SerializedName("curve")
    @Expose
    var curve: EllipticCurveType.ELLIPTIC_CURVE_TYPE? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("candidate")
    @Expose
    var candidate: Ciphers? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer {

    data class Ciphers(
        @SerializedName("ciphers")
        @Expose
        var ciphers: List<SymmetricCipherType.SYMMETRIC_CIPHER_TYPE>? = null
    )
}
```

### Property

| Name        | Type                             | Description                               | **M/O** | **Note** |
|-------------|----------------------------------|-------------------------------------------|---------|----------|
| client      | String                           | Client DID                         |    M    |          |
| clientNonce | String                           | Client Nonce              |    M    |          |
| curve       | ELLIPTIC_CURVE_TYPE | Curve type for ECDH                       |    M    |          |
| publicKey   | String                           | Public key for ECDH                       |    M    |          |
| candidate   | ReqEcdh.Ciphers                  | Candidate ciphers                         |    O    |          |
| proof       | Proof                            | Proof                   |    M    | [Proof](#4-proof) |
| proofs      | List<Proof>                      | List of proofs                            |    O    | [Proof](#4-proof) |

<br>

## 3.2. AccEcdh

### Description

`ECDH acceptance data`

### Declaration

```kotlin
data class AccEcdh(
    @SerializedName("server")
    @Expose
    var server: String? = null,

    @SerializedName("serverNonce")
    @Expose
    var serverNonce: String? = null,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("cipher")
    @Expose
    var cipher: SymmetricCipherType.SYMMETRIC_CIPHER_TYPE? = null,

    @SerializedName("padding")
    @Expose
    var padding: SymmetricPaddingType.SYMMETRIC_PADDING_TYPE? = null,

    @SerializedName("proof")
    @Expose
    var proof: Proof? = null
)
```

### Property

| Name        | Type                                       | Description                        | **M/O** | **Note** |
|-------------|--------------------------------------------|------------------------------------|---------|----------|
| server      | String                                     | Server ID                  |    M    |          |
| serverNonce | String                                     | Server Nonce                       |    M    |          |
| publicKey   | String                                     | Public Key for key agreement       |    M    |          |
| cipher      | SymmetricCipherType.SYMMETRIC_CIPHER_TYPE  | Cipher type for encryption         |    M    |          |
| padding     | SymmetricPaddingType.SYMMETRIC_PADDING_TYPE| Padding type for encryption        |    M    |          |
| proof       | Proof                                      | Key agreement proof                |    O    | [Proof](#4-proof) |

<br>

## 3.3. AccE2e

### Description

`E2E acceptance data`

### Declaration

```kotlin
data class AccE2e(
    @SerializedName("publicKey")
    @Expose
    var publicKey: String? = null,

    @SerializedName("iv")
    @Expose
    var iv: String? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer
```

### Property

| Name       | Type               | Description                | **M/O** | **Note**               |
|------------|--------------------|----------------------------|---------|------------------------|
| publicKey  | String              | Public Key for encryption  |    M    | |
| iv         | String              | Initialize Vector      |    M    | |
| proof      | Proof               | Key agreement proof        |    O    | [Proof](#4-proof)       |
| proofs     | List\<Proof>         | List of proofs             |    O    | [Proof](#4-proof)       |

<br>

## 3.4. E2e

### Description

`E2E encryption information`

### Declaration

```kotlin
data class E2e(
    @SerializedName("iv")
    @Expose
    var iv: String? = null,

    @SerializedName("encVc")
    @Expose
    var encVc: String? = null
)
```

### Property

| Name  | Type   | Description                      | **M/O** | **Note** |
|-------|--------|----------------------------------|---------|----------|
| iv    | String | Initialize vector       |    M    |          |
| encVc | String | Encrypted Verifiable Credential  |    M    |          |

<br>

## 3.5. DIDAuth

### Description

`DID Authentication data`

### Declaration

```kotlin
data class DIDAuth(
    @SerializedName("did")
    @Expose
    var did: String? = null,

    @SerializedName("authNonce")
    @Expose
    var authNonce: String? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : SortData(), ProofContainer
```

### Property

| Name       | Type       | Description                        | **M/O** | **Note**                  |
|------------|------------|------------------------------------|---------|---------------------------|
| did        | String     | DID     |    M    |                            |
| authNonce  | String     | Auth nonce      |    M    |                            |
| proof      | Proof      | Authentication proof               |    M    | [Proof](#4-proof)          |
| proofs     | List\<Proof>| List of authentication proofs      |    M    | [Proof](#4-proof)          |

<br>

## 4. DidDoc
## 4.1. DidDocVo

### Description

`Encoded DID document`

### Declaration

```kotlin
data class DidDocVo(
    @SerializedName("didDoc")
    var didDoc: String? = null
)
```

### Property

| Name   | Type   | Description            | **M/O** | **Note** |
|--------|--------|------------------------|---------|----------|
| didDoc | String | Encoded DID document |    M    |          |

<br>

## 4.2. AttestedDidDoc

### Description

`Attested DID information`

### Declaration

```kotlin
data class AttestedDidDoc(
    @SerializedName("walletId")
    @Expose
    var walletId: String? = null,

    @SerializedName("ownerDidDoc")
    @Expose
    var ownerDidDoc: String? = null,

    @SerializedName("provider")
    @Expose
    var provider: Provider? = null,

    @SerializedName("nonce")
    @Expose
    var nonce: String? = null,

    @SerializedName("proof")
    @Expose
    var proof: Proof? = null
)
```

### Property

| Name       | Type     | Description                     | **M/O** | **Note**                  |
|------------|----------|---------------------------------|---------|---------------------------|
| walletId   | String   | Wallet ID               |    M    |                            |
| ownerDidDoc| String   | Owner's DID document            |    M    |                            |
| provider   | Provider | Provider information            |    M    | [Provider](#2111-provider)      |
| nonce      | String   | Nonce |    M    |                            |
| proof      | Proof    | Attestation proof               |    M    | [Proof](#4-proof)          |

<br>

## 4.3. SignedDidDoc

### Description

`Signed DID Document`

### Declaration

```kotlin
data class SignedDidDoc(
    @SerializedName("ownerDidDoc")
    @Expose
    var ownerDidDoc: String? = null,

    @SerializedName("wallet")
    @Expose
    var wallet: Wallet? = null,

    @SerializedName("nonce")
    @Expose
    var nonce: String? = null,

    @SerializedName("proof")
    @Expose
    var _proof: Proof? = null,

    @SerializedName("proofs")
    @Expose
    var _proofs: List<Proof>? = null
) : ProofContainer
```

### Property

| Name       | Type           | Description                | **M/O** | **Note** |
|------------|----------------|----------------------------|---------|----------|
| ownerDidDoc| String          | Owner's DID document       |    M    |          |
| wallet     | Wallet          | Wallet information         |    M    | [Wallet](#2121-wallet) |
| nonce      | String          | Nonce                |    M    |          |
| proof      | Proof           | Proof               |    M    | [Proof](#4-proof) |
| proofs     | List\<Proof>     | List of proofs             |    M    | [Proof](#4-proof) |

<br>

## 5. Offer
## 5.1. IssueOfferPayload

### Description

`Payload for issuing an offer`

### Declaration

```kotlin
data class IssueOfferPayload(
    @SerializedName("offerId")
    @Expose
    var offerId: String? = null,

    @SerializedName("type")
    @Expose
    var type: OFFER_TYPE? = null,

    @SerializedName("vcPlanId")
    @Expose
    var vcPlanId: String? = null,

    @SerializedName("issuer")
    @Expose
    var issuer: String? = null,

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null
)
```

### Property

| Name      | Type   | Description                   | **M/O** | **Note** |
|-----------|--------|-------------------------------|---------|----------|
| offerId   | String | Offer ID |    M    |          |
| vcPlanId  | String | VC Plan ID  |    M    |          |
| issuer    | String | Issuer DID      |    M    |          |
| validUntil| String | Expiration date          |    M    |          |

<br>

## 5.2. VerifyOfferPayload

### Description

`Payload for verifying an offer`

### Declaration

```kotlin
data class VerifyOfferPayload(
    @SerializedName("offerId")
    @Expose
    var offerId: String? = null,

    @SerializedName("type")
    @Expose
    var type: OFFER_TYPE? = null,

    @SerializedName("mode")
    @Expose
    var mode: PRESENT_MODE? = null,

    @SerializedName("device")
    @Expose
    var device: String? = null,

    @SerializedName("service")
    @Expose
    var service: String? = null,

    @SerializedName("endpoints")
    @Expose
    var endpoints: List<String>? = null,

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null,

    @SerializedName("locked")
    @Expose
    var locked: Boolean? = null

) {
    enum class OFFER_TYPE(val value: String) {
        IssueOffer("IssueOffer"),
        VerifyOffer("VerifyOffer");
    }
    enum class PRESENT_MODE(val value: String) {
        Direct("direct mode"),
        Indirect("indirect mode"),
        Proxy("proxy");
    }
}
```

### Property

| Name       | Type                  | Description                           | **M/O** | **Note** |
|------------|-----------------------|---------------------------------------|---------|----------|
| offerId    | String                 | Offer ID              |    M    |          |
| type       | OFFER_TYPE  | Offer type            |    M    |          |
| mode       | PRESENT_MODE | Presentation mode           |    M    |          |
| device     | String                 | Device identifier      |    O    |          |
| service    | String                 | Service identifier     |    O    |          |
| endpoints  | List\<String>           | List of profile request API endpoints             |    O    |          |
| validUntil | String                 | End date of the offer        |    M    |          |
| locked     | Boolean                | Offer locked status    |    O    |          |

<br>

## 6. VC
## 6.1. ReqVC

### Description

`Request object for VC`

### Declaration

```kotlin
data class ReqVC(
    @SerializedName("refId")
    @Expose
    var refId: String? = null,

    @SerializedName("profile")
    @Expose
    var profile: Profile? = null
)
```

### Property

| Name         | Type            | Description                         | **M/O** | **Note** |
|--------------|-----------------|-------------------------------------|---------|----------|
| refId        | String           | Reference ID       |    M    |          |
| profile      | Profile    | Request issue profile      |    M    |          |

<br>

## 6.1.1. Profile

### Description

`Request issue profile`

### Declaration

```kotlin
data class Profile(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("issuerNonce")
    @Expose
    var issuerNonce: String? = null
)
```

### Property

| Name         | Type            | Description                         | **M/O** | **Note** |
|--------------|-----------------|-------------------------------------|---------|----------|
| id        | String | Issuer DID                        |    M    |                          |
| issuerNonce | String            | Issuer nonce        |    M    |                                     |
<br>


## 6.2. VCPlanList

### Description

`List of VC plan`

### Declaration

```kotlin
data class VCPlanList(
    @SerializedName("count")
    @Expose
    var count: Int = 0,

    @SerializedName("items")
    @Expose
    var items: List<VCPlan> = emptyList()
)
```

### Property

| Name   | Type             | Description                      | **M/O** | **Note** |
|--------|------------------|----------------------------------|---------|----------|
| count  | int              | Number of VC plan list               |    M    |          |
| items  | List\<VCPlan>     | List of VC plan                |    M    | [VCPlan](#621-vcplan) |

<br>

## 6.2.1. VCPlan

### Description

`Details of VC plan`

### Declaration

```kotlin
data class VCPlan(
    @SerializedName("vcPlanId")
    @Expose
    var vcPlanId: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("ref")
    @Expose
    var ref: String? = null,

    @SerializedName("logo")
    @Expose
    var logo: LogoImage? = null,

    @SerializedName("validFrom")
    @Expose
    var validFrom: String? = null,

    @SerializedName("validUntil")
    @Expose
    var validUntil: String? = null,

    @SerializedName("credentialSchema")
    @Expose
    var credentialSchema: CredentialSchema? = null,

    @SerializedName("option")
    @Expose
    var option: Option? = null,

    @SerializedName("allowedIssuers")
    @Expose
    var allowedIssuers: List<String>? = null,

    @SerializedName("manager")
    @Expose
    var manager: String? = null
)
```

### Property

| Name           | Type               | Description                          | **M/O** | **Note** |
|----------------|--------------------|--------------------------------------|---------|----------|
| vcPlanId       | String             | VC plan ID          |    M    |          |
| name           | String             | VC plan name                 |    M    |          |
| description    | String             | VC plan description           |    M    |          |
| ref            | String             | Reference ID          |    M    |          |
| logo           | LogoImage          | Logo image        |    O    | [LogoImage](#53-logoimage) |
| validFrom      | String             | Validity start date      |    M    |          |
| validUntil     | String             | Validity end date      |    M    |          |
| credentialSchema| CredentialSchema  | Credential schema            |    O    |          |
| option         | Option      | VC Plan options                         |    O    |          |
| allowedIssuers | List<String>       | List of issuer DIDs allowed to use the VC plan |    M    |          |
| manager        | String             | Entity with management authority over the VC plan    |    M    |          |

## 6.2.1.1. Option

### Description

`VC plan Option`

### Declaration

```kotlin
data class Option(
    var allowUserInit: Boolean = false,
    var allowIssuerInit: Boolean = false,
    var delegatedIssuance: Boolean = false
)
```

### Property

| Name         | Type            | Description                         | **M/O** | **Note** |
|--------------|-----------------|-------------------------------------|---------|----------|
| allowUserInit        | Boolean | Whether user-initiated issuance is allowed                        |    M    |                          |
| allowIssuerInit | Boolean            | Whether issuer-initiated issuance is allowed        |    M    |                                     |
| delegatedIssuance | Boolean            | Whether delegated issuance by a representative issuer is allowed         |    M    |                                     |

<br>


# Enumerators

## 1. DID_KEY_TYPE

### Description

`DID key types`

### Declaration
```kotlin
enum class DID_KEY_TYPE(private val value: String) {
    rsaVerificationKey2018("RsaVerificationKey2018"),
    secp256k1VerificationKey2018("Secp256k1VerificationKey2018"),
    secp256r1VerificationKey2018("Secp256r1VerificationKey2018");
}
```
<br>

## 2. DID_SERVICE_TYPE

### Description

`Service types`

### Declaration
```kotlin
enum class DID_SERVICE_TYPE(val value: String) {
    linkedDomains("LinkedDomains"),
    credentialRegistry("CredentialRegistry");
}
```

<br>

## 3. PROOF_PURPOSE

### Description

`Proof purposes`

### Declaration
```kotlin
enum class PROOF_PURPOSE(val value: String) {
    assertionMethod("assertionMethod"),
    authentication("authentication"),
    keyAgreement("keyAgreement"),
    capabilityInvocation("capabilityInvocation"),
    capabilityDelegation("capabilityDelegation");
}
```

<br>

## 4. PROOF_TYPE

### Description

`Proof types`

### Declaration

```kotlin
enum class PROOF_TYPE(val value: String) {
    rsaSignature2018("RsaSignature2018"),
    secp256k1Signature2018("Secp256k1Signature2018"),
    secp256r1Signature2018("Secp256r1Signature2018");
}
```

<br>

## 5. AUTH_TYPE

### Description

`Indicates the method of accessing the key`

### Declaration

```kotlin
enum class AUTH_TYPE(val intValue: Int) {
    FREE(1),
    PIN(2),
    BIO(4);
}
```

<br>

## 6. EVIDENCE_TYPE

### Description

`Evidence Enumerator for Multi-type Arrays`

### Declaration

```kotlin
enum class EVIDENCE_TYPE(val value: String) {
    documentVerification("DocumentVerification");
}
```

<br>

## 7. PRESENCE

### Description

`Presence types`

### Declaration

```kotlin
enum class PRESENCE(val value: String) {
    physical("Physical"),
    digital("Digital");
}
```

<br>

## 8. PROFILE_TYPE

### Description

`Profile types`

### Declaration

```kotlin
enum class PROFILE_TYPE(val value: String) {
    issueProfile("IssueProfile"),
    verifyProfile("VerifyProfile");
}
```

<br>

## 9. LOGO_IMAGE_TYPE

### Description

`Logo image types`

### Declaration

```kotlin
enum class LOGO_IMAGE_TYPE(val value: String) {
    jpg("jpg"),
    png("png");
}
```

<br>

## 10. CLAIM_TYPE

### Description

`Claim types`

### Declaration

```kotlin
enum class CLAIM_TYPE(val value: String) {
    text("text"),
    image("image"),
    document("document");
}
```

<br>

## 11. CLAIM_FORMAT

### Description

`Claim formats`

### Declaration

```kotlin
enum class CLAIM_FORMAT(val value: String) {
    plain("plain"),
    html("html"),
    xml("xml"),
    csv("csv"),
    png("png"),
    jpg("jpg"),
    gif("gif"),
    txt("txt"),
    pdf("pdf"),
    word("word");
}
```

<br>

## 12. LOCATION

### Description

`Value location`

### Declaration

```kotlin
enum class LOCATION(val value: String) {
    @SerializedName("inline")
    INLINE("inline"),
    @SerializedName("remote")
    REMOTE("remote"),
    @SerializedName("attach")
    ATTACH("attach");
}
```

<br>

## 13. SYMMETRIC_PADDING_TYPE

### Description

`Padding options for symmetric encryption`

### Declaration

```kotlin
enum class SYMMETRIC_PADDING_TYPE(val value: String) {
    NOPAD("NOPAD"),
    PKCS5("PKCS5");
}
```

<br>

## 14. SYMMETRIC_CIPHER_TYPE

### Description

`Types of symmetric encryption algorithms`

### Declaration

```kotlin
enum class SYMMETRIC_CIPHER_TYPE(val value: String) {
    AES128CBC("AES-128-CBC"),
    AES128ECB("AES-128-ECB"),
    AES256CBC("AES-256-CBC"),
    AES256ECB("AES-256-ECB");
}
```

<br>

## 15. ALGORITHM_TYPE

### Description

`Types of algorithms`

### Declaration

```kotlin
enum class ALGORITHM_TYPE(val value: String) {
    RSA("Rsa"),
    SECP256K1("Secp256k1"),
    SECP256R1("Secp256r1");
}
```

<br>

## 16. CREDENTIAL_SCHEMA_TYPE

### Description

`Types of credential schemas`

### Declaration

```kotlin
enum class CREDENTIAL_SCHEMA_TYPE(val value: String) {
    osdSchemaCredential("OsdSchemaCredential");
}
```

<br>

## 17. ELLIPTIC_CURVE_TYPE

### Description

`Types of elliptic curves`

### Declaration

```kotlin
enum class ELLIPTIC_CURVE_TYPE(val value: String) {
    SECP256K1("Secp256k1"),
    SECP256R1("Secp256r1");
}
```

<br>

## 18. VERIFY_AUTH_TYPE

### Description

`Indicates access methods and submission options for keys. Similar to AuthType.`

### Declaration

```kotlin
enum class VERIFY_AUTH_TYPE(val intValue: Int) {
    ANY(0x00000000),
    FREE(0x00000001),
    PIN(0x00000002),
    BIO(0x00000004),
    PIN_OR_BIO(0x00000006),
    PIN_AND_BIO(0x00008006);
}
```

<br>

## 19. ROLE_TYPE

### Description

`role types`

### Declaration

```kotlin
enum class ROLE_TYPE(val value: String) {
    TAS("Tas"),
    WALLET("Wallet"),
    ISSUER("Issuer"),
    VERIFIER("Verifier"),
    WALLET_PROVIDER("WalletProvider"),
    APP_PROVIDER("AppProvider"),
    LIST_PROVIDER("ListProvider"),
    OP_PROVIDER("OpProvider"),
    KYC_PROVIDER("KycProvider"),
    NOTIFICATION_PROVIDER("NotificationProvider"),
    LOG_PROVIDER("LogProvider"),
    PORTAL_PROVIDER("PortalProvider"),
    DELEGATION_PROVIDER("DelegationProvider"),
    STORAGE_PROVIDER("StorageProvider"),
    BACKUP_PROVIDER("BackupProvider"),
    ETC("Etc");
}
```
<br>

## 20. SERVER_TOKEN_PURPOSE

### Description

`Type of server token purposes`

### Declaration

```kotlin
enum class SERVER_TOKEN_PURPOSE(val intValue: Int) {
    CREATE_DID(5),
    UPDATE_DID(6),
    RESTORE_DID(7),
    ISSUE_VC(8),
    REMOVE_VC(9),
    PRESENT_VP(10),
    CREATE_DID_AND_ISSUE_VC(13);
}
```

<br>

## 21. WALLET_TOKEN_PURPOSE

### Description

`Type of wallet token purposes`

### Declaration

```kotlin
enum class WALLET_TOKEN_PURPOSE(val intValue: Int) {
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
}
```




# APIs

## 1. deserialize

### Description

`Deserializes a JSON string into a specified data model object.`

### Declaration

```kotlin
fun <T> deserialize(jsonString: String, clazz: Class<T>): T
```

<br>

## 2. convertFrom

### Description

`Converts an AlgorithmType into corresponding enum values(ELLIPTIC_CURVE_TYPE / PROOF_TYPE / DID_KEY_TYPE)`

### Declaration

```kotlin
@JvmStatic
fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): ELLIPTIC_CURVE_TYPE = when (type) {
    AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> SECP256K1
    AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> SECP256R1
    else -> throw RuntimeException()
}

@JvmStatic
fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): PROOF_TYPE = when (type) {
    AlgorithmType.ALGORITHM_TYPE.RSA -> rsaSignature2018
    AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> secp256k1Signature2018
    AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> secp256r1Signature2018
}

@JvmStatic
fun convertFrom(type: AlgorithmType.ALGORITHM_TYPE): DID_KEY_TYPE {
    return when (type) {
        AlgorithmType.ALGORITHM_TYPE.RSA -> rsaVerificationKey2018
        AlgorithmType.ALGORITHM_TYPE.SECP256K1 -> secp256k1VerificationKey2018
        AlgorithmType.ALGORITHM_TYPE.SECP256R1 -> secp256r1VerificationKey2018
    }
}
```

<br>

## 3. convertTo

### Description

`Converts from (ELLIPTIC_CURVE_TYPE / PROOF_TYPE / DID_KEY_TYPE) to the corresponding AlgorithmType enum value.`

### Declaration

```kotlin
@JvmStatic
fun convertTo(type: ELLIPTIC_CURVE_TYPE): AlgorithmType.ALGORITHM_TYPE = when (type) {
    SECP256K1 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
    SECP256R1 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
}

@JvmStatic
fun convertTo(type: PROOF_TYPE): AlgorithmType.ALGORITHM_TYPE = when (type) {
    rsaSignature2018 -> AlgorithmType.ALGORITHM_TYPE.RSA
    secp256k1Signature2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
    secp256r1Signature2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
}

@JvmStatic
fun convertTo(type: DID_KEY_TYPE): AlgorithmType.ALGORITHM_TYPE {
    return when (type) {
        rsaVerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.RSA
        secp256k1VerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256K1
        secp256r1VerificationKey2018 -> AlgorithmType.ALGORITHM_TYPE.SECP256R1
    }
}
```

<br>





