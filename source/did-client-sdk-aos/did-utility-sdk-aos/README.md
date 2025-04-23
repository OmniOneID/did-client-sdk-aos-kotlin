# Android Utility SDK Guide
This document is a guide for using the Utility SDK, providing various encryption, encoding, and hashing functions.

## S/W Specifications
| Category | Details                |
|------|----------------------------|
| OS  | Android 13|
| Language  | Java 17 through 21, Kotlin 1.9.22|
| IDE  | Android Studio 4|
| Build System  | Gradle 8.2 |
| Compatibility | Android API level 34 or higher  |

<br>

## Build Method
: Execute the export JAR task in the build.gradle file of this SDK project to generate the JAR file.
1. Open the `build.gradle.kts` file of the project and add the following `export JAR` task.
```kotlin
val customVersionMajorMinorBuild = "1.0.0"

tasks.register<Copy>("exportJar") {
    from("build/intermediates/runtime_library_classes_jar/debug/classes.jar")
    into("../release")
    include("classes.jar")
    rename("classes.jar", "${project.name}-${customVersionMajorMinorBuild}.jar")
}
```

2. Open the `Gradle` window in Android Studio, and run the `Tasks > other > exportJar` task for the project.
3. Once execution is complete, the `did-utility-sdk-aos-1.0.0.jar` file will be generated in the `release/` folder.

<br>

## How to Apply SDK
1. Copy the `did-utility-sdk-aos-1.0.0.jar` file to the `libs` directory of your app project.
2. Add the following dependencies to the build.gradle file of your app project.

```groovy
    implementation files('libs/did-utility-sdk-aos-1.0.0.jar')
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.bitcoinj:bitcoinj-core:0.15.7'

    implementation 'com.madgag.spongycastle:core:1.54.0.0'
    implementation 'com.madgag.spongycastle:prov:1.54.0.0'
    implementation 'com.madgag.spongycastle:pkix:1.54.0.0'
    implementation 'com.madgag.spongycastle:pg:1.54.0.0'
```

3. Synchronize `Gradle` to ensure that dependencies have been added correctly.

<br>

## API Specifications

| Category       | API Documentation Link          |
|----------------|--------------------------------|
| CryptoUtils    | [Utility SDK API](../../../docs/api/did-utility-sdk-aos/Utility.md) |
| MultibaseUtils | 〃                             |
| DigestUtils    | 〃                             |
| ErrorCode      | [Error Code](../../../docs/api/did-utility-sdk-aos/UtilityError.md) |

### CryptoUtils

CryptoUtils provides AES encryption and decryption of data, PBKDF, and generates Shared Secrets for ECDH. 
<br> Key features include:
- Data encryption and decryption using the AES algorithm
- Key generation using PBKDF (Password-Based Key Derivation Function)
- Shared Secrets Generation for ECDH (Elliptic-curve Diffie-Hellman)

### MultibaseUtils

MultibaseUtils offers encoding and decoding for base16, base58, base64, and base64url. <br> Key features include:
- Efficient conversion of data through support for various encoding formats
- Versatile and flexible data handling through Base encoding

### DigestUtils
DigestUtils provides hash functions such as SHA-256, SHA-384, and SHA-512. Key features include:
- Data integrity verification and encryption using various hash algorithms
- Hash-based security features for secure data transmission and storage