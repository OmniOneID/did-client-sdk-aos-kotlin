# Android Core SDK Guide
본 문서는 OpenDID Wallet Core SDK 사용을 위한 가이드로, 
Open DID에 필요한 Key, DID Document(DID 문서), Verifiable Credential(이하 VC) 정보를 생성 및 보관, 관리하는 기능을 제공한다.


## S/W 사양
| 구분 | 내용                |
|------|----------------------------|
| OS  | Android 13|
| Language  | Java 17, Kotlin 1.9.22|
| IDE  | Android Studio 4|
| Build System  | Gradle 8.2 |
| Compatibility | Android API level 34 or higher  |

<br>

## 빌드 방법
: 본 SDK 프로젝트의 build.gradle 파일에서 export JAR 태스크를 실행하여 JAR 파일을 생성한다.
1. 프로젝트의 `build.gradle.kts` 파일을 열고, 아래와 같은 `export JAR` 태스크를 추가한다.
```kotlin
val customVersionMajorMinorBuild = "1.0.0"

tasks.register<Copy>("exportJar") {
    from("build/intermediates/runtime_library_classes_jar/debug/classes.jar")
    into("../release")
    include("classes.jar")
    rename("classes.jar", "${project.name}-${customVersionMajorMinorBuild}.jar")
}
```
2. Android Studio에서 `Gradle` 창을 열고, 프로젝트의 `Tasks > other > exportJar` 태스크를 실행한다.
3. 실행이 완료되면 `release/` 폴더에 `did-core-sdk-aos-1.0.0.jar` 파일을 생성한다.

<br>

## SDK 적용 방법
1. 앱 프로젝트의 libs에 `did-core-sdk-aos-1.0.0.jar`, `did-utility-sdk-aos-1.0.0.jar`, `did-datamodel-sdk-aos-1.0.0.jar` 파일을 복사한다.
2. 앱 프로젝트의 build gradle에 아래 의존성을 추가한다.

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
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인한다.

<br>

## API 규격서
| 구분 | API 문서 Link |
|------|----------------------------|
| KeyManager  | [Core SDK - KeyManager API](../../../docs/api/did-core-sdk-aos/KeyManager_ko.md) |
| DIDManager  | [Core SDK - DIDManager API](../../../docs/api/did-core-sdk-aos/DIDManager_ko.md) |
| VCManager  | [Core SDK - VCManager API](../../../docs/api/did-core-sdk-aos/VCManager_ko.md)  |
| SecureEncryptor | [Core SDK - SecureEncryptor API](../../../docs/api/did-core-sdk-aos/SecureEncryptor_ko.md)  |
| ErrorCode      | [Error Code](../../../docs/api/did-core-sdk-aos/WalletCoreError.md) |

### KeyManager
KeyManager는 서명용 키쌍을 생성하고 관리하며, 이를 안전하게 저장하는 기능을 제공한다.<br>주요 기능은 다음과 같다.

* <b>키 생성</b>: 새로운 키쌍을 생성한다.
* <b>키 저장</b>: 생성된 키를 안전하게 저장한다.
* <b>키 조회</b>: 저장된 키를 조회한다.
* <b>키 삭제</b>: 저장된 키를 삭제한다.

### DIDManager
DIDManager는 DID Document를 생성하고 관리하는 기능을 제공한다.<br>
주요 기능은 다음과 같다.

* <b>DID 생성</b>: 새로운 DID를 생성한다.
* <b>DID Document 관리</b>: DID Document를 생성하고, 업데이트하며, 삭제한다.
* <b>DID Document 조회</b>: DID Document에 대한 정보를 조회한다.
  
### VCManager
VCManager는 Verifiable Credential(VC)을 관리하고 저장하는 기능을 제공한다.<br>
주요 기능은 다음과 같다.

* <b>VC 저장</b>: 생성된 VC를 안전하게 저장한다.
* <b>VC 조회</b>: 저장된 VC를 조회한다.
* <b>VC 삭제</b>: 저장된 VC를 삭제한다.

### SecureEncryptor
SecureEncryptor는 Keystore를 이용하여 데이터를 암호화하고 복호화하는 기능을 제공한다.<br>주요 기능은 다음과 같다.

* <b>데이터 암호화</b>: 데이터를 안전하게 암호화한다.
* <b>데이터 복호화</b>: 암호화된 데이터를 복호화한다.
