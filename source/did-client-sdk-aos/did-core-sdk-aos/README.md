# Android Core SDK Guide
This document is a guide for using the OpenDID Wallet Core SDK, providing functions to generate, store, and manage the keys, DID Document, and Verifiable Credential (VC) information required for Open DID.


## S/W Specifications
| Category | Details                |
|------|----------------------------|
| OS  | Android 13|
| Language  | Java 17, Kotlin 1.9.22|
| IDE  | Android Studio 4|
| Build System  | Gradle 8.2 |
| Compatibility | Android API level 34 or higher  |

<br>


## Build Method
: Execute the export JAR task in the build.gradle file of this SDK project to generate a JAR file.
1. Open the project's `build.gradle.kts` file and add the following `export JAR` task.
```kotlin
val customVersionMajorMinorBuild = "1.0.0"

tasks.register<Copy>("exportJar") {
    from("build/intermediates/runtime_library_classes_jar/debug/classes.jar")
    into("../release")
    include("classes.jar")
    rename("classes.jar", "${project.name}-${customVersionMajorMinorBuild}.jar")
}
```
2. Open the `Gradle` window in Android Studio, and run the `Tasks > other > exportJar` task of the project.
3. Once the execution is complete, the `did-core-sdk-aos-1.0.0.jar` file will be generated in the `release/` folder.

<br>

## SDK Application Method
1. Copy the `did-core-sdk-aos-1.0.0.jar`, `did-utility-sdk-aos-1.0.0.jar`, `did-datamodel-sdk-aos-1.0.0.jar` file to the libs of the app project.
2. Add the following dependencies to the build.gradle of the app project.

```groovy
    implementation files('libs/did-core-sdk-aos-1.0.0.jar')
    implementation files('libs/did-utility-sdk-aos-1.0.0.jar')
    implementation files('libs/did-datamodel-sdk-aos-1.0.0.jar')
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'androidx.biometric:biometric:1.1.0'

    implementation 'com.madgag.spongycastle:core:1.54.0.0'
    implementation 'com.madgag.spongycastle:prov:1.54.0.0'
    implementation 'com.madgag.spongycastle:pkix:1.54.0.0'
    implementation 'com.madgag.spongycastle:pg:1.54.0.0'
```
3. Sync `Gradle` to ensure the dependencies are properly added.

<br>

## API Specification
| Category | API Document Link |
|------|----------------------------|
| KeyManager  | [Core SDK - KeyManager API](../../../docs/api/did-core-sdk-aos/KeyManager.md) |
| DIDManager  | [Core SDK - DIDManager API](../../../docs/api/did-core-sdk-aos/DIDManager.md) |
| VCManager  | [Core SDK - VCManager API](../../../docs/api/did-core-sdk-aos/VCManager.md)  |
| SecureEncryptor | [Core SDK - SecureEncryptor API](../../../docs/api/did-core-sdk-aos/SecureEncryptor.md)  |
| ErrorCode      | [Error Code](../../../docs/api/did-core-sdk-aos/WalletCoreError.md) |

### KeyManager
KeyManager provides the functionality to generate and manage key pairs for signing and store them securely.<br>The main features are as follows:

* <b>Key Generation</b>: Generates a new key pair.
* <b>Key Storage</b>: Securely stores the generated key.
* <b>Key Retrieval</b>: Retrieves the stored key.
* <b>Key Deletion</b>: Deletes the stored key.

### DIDManager
DIDManager provides the functionality to generate and manage DID Documents.<br>The main features are as follows:

* <b>DID Generation</b>: Generates a new DID.
* <b>DID Document Management</b>: Creates, updates, and deletes DID Documents.
* <b>DID Document Retrieval</b>: Retrieves information about DID Documents.

### VCManager
VCManager provides the functionality to manage and store Verifiable Credentials (VC).<br>The main features are as follows:

* <b>VC Storage</b>: Securely stores the generated VC.
* <b>VC Retrieval</b>: Retrieves the stored VC.
* <b>VC Deletion</b>: Deletes the stored VC.

### SecureEncryptor
SecureEncryptor provides the functionality to encrypt and decrypt data using the Keystore.<br>The main features are as follows:

* <b>Data Encryption</b>: Securely encrypts the data.
* <b>Data Decryption</b>: Decrypts the encrypted data.