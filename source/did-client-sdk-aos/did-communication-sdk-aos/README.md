# Android Communication SDK Guide
This document is a guide for using the OpenDID Communication SDK, which provides a communication interface to manage HTTP requests and responses, supporting GET and POST methods with JSON payloads.


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
3. Once the execution is complete, the `did-communication-sdk-aos-1.0.0.jar` file will be generated in the `release/` folder.

<br>

## SDK Application Method
1. Copy the `did-communication-sdk-aos-1.0.0.jar` file to the libs of the app project.
2. Add the following dependencies to the build.gradle of the app project.

```groovy
    implementation files('libs/did-communication-sdk-aos-1.0.0.jar')
```
3. Sync `Gradle` to ensure the dependencies are properly added.

<br>

## API Specification
| Category | API Document Link |
|------|----------------------------|
| Communication  | [Communication SDK API](../../../docs/api/did-communication-sdk-aos/Communication.md) |
| ErrorCode      | [Error Code](../../../docs/api/did-communication-sdk-aos/CommunicationError.md) |