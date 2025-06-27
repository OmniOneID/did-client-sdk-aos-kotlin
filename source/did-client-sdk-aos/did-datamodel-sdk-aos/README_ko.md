# Android DataModel SDK Guide
본 문서는 DataModel SDK 사용을 위한 가이드로, 앱과 월렛 SDK에서 사용하는 데이터 모델을 정의한다.



## S/W 사양
| 구분 | 내용                |
|------|----------------------------|
| OS  | Android 13|
| Language  | Java 17 through 21, Kotlin 1.9.22|
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
3. 실행이 완료되면 `release/` 폴더에 `did-datamodel-sdk-aos-1.0.0.jar` 파일을 생성한다.

<br>

## SDK 적용 방법
1. 앱 프로젝트의 libs에 `did-datamodel-sdk-aos-1.0.0.jar` 파일을 복사한다.
2. 앱 프로젝트의 build gradle에 아래 의존성을 추가한다.

```groovy
    implementation files('libs/did-datamodel-sdk-aos-1.0.0.jar')
    implementation 'com.google.code.gson:gson:2.10.1'
```
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인한다.

<br>


## Structure 규격서
| 구분          | Structure 문서 Link                  |
|---------------|-------------------------------|
| DataModel   | [DataModel SDK](../../../docs/api/did-datamodel-sdk-aos/DataModel_ko.md) |