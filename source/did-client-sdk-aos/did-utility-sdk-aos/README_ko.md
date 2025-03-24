# Android Utility SDK Guide
본 문서는 Utility SDK 사용을 위한 가이드로, 다양한 암호화 및 인코딩, 해싱 기능을 제공한다.



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
3. 실행이 완료되면 `release/` 폴더에 `did-utility-sdk-aos-1.0.0.jar` 파일을 생성한다.

<br>


## SDK 적용 방법
1. 앱 프로젝트의 libs에 `did-utility-sdk-aos-1.0.0.jar` 파일을 복사한다.
2. 앱 프로젝트의 build gradle에 아래 의존성을 추가한다.

```groovy
    implementation files('libs/did-utility-sdk-aos-1.0.0.jar')
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.bitcoinj:bitcoinj-core:0.15.7'

    implementation 'com.madgag.spongycastle:core:1.54.0.0'
    implementation 'com.madgag.spongycastle:prov:1.54.0.0'
    implementation 'com.madgag.spongycastle:pkix:1.54.0.0'
    implementation 'com.madgag.spongycastle:pg:1.54.0.0'
```
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인한다.

<br>


## API 규격서
| 구분          | API 문서 Link                  |
|---------------|-------------------------------|
| CryptoUtils   | [Utility SDK API](../../../docs/api/did-utility-sdk-aos/Utility_ko.md) |
| MultibaseUtils | 〃                             |
| DigestUtils    | 〃                             |
| ErrorCode      | [Error Code](../../../docs/api/did-utility-sdk-aos/UtilityError.md) |

### CryptoUtils
CryptoUtils는 데이터의 AES 암호화 및 복호화, PBKDF, ECDH를 위한 Shared Secret 생성 기능을 제공한다. <br> 주요 기능은 다음과 같다.
- AES 알고리즘을 사용한 데이터 암호화 및 복호화
- PBKDF (Password-Based Key Derivation Function)를 사용한 키 생성
- ECDH (Elliptic-curve Diffie-Hellman)를 위한 Shared Secret 생성

### MultibaseUtils
MultibaseUtils는 base16, base58, base64, base64url 인코딩 및 디코딩을 제공한다. <br>주요 기능은 다음과 같다.
- 다양한 인코딩 형식을 지원하여 데이터의 효율적인 전환 제공
- Base 인코딩의 확장성과 유연성을 통한 다양한 데이터 처리

### DigestUtils
DigestUtils는 SHA-256, SHA-384, SHA-512 등의 해시 함수를 제공한다. 주요 기능은 다음과 같다.
- 다양한 해시 알고리즘을 사용하여 데이터의 무결성 검증 및 암호화
- 안전한 데이터 전송 및 저장을 위한 해시 기반 보안 기능 제공