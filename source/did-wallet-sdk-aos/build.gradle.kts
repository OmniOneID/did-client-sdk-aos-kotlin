plugins {
    id("com.android.library") version "8.2.1"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    kotlin("kapt") version "1.9.20"
}

android {
    namespace = "org.omnione.did.sdk.wallet"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["notPackage"] = "org.bouncycastle.pqc.crypto.qtesla"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

val customVersion = "1.1.0"

tasks.register<Copy>("exportJar") {
    from("build/intermediates/aar_main_jar/debug/classes.jar") // 최신 AGP 기준
    into("../release")
    include("classes.jar")
    rename("classes.jar", "${project.name}-${customVersion}.jar")
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Bitcoin / Security
    implementation("org.bitcoinj:bitcoinj-core:0.15.7")
    implementation("com.madgag.spongycastle:core:1.54.0.0")
    implementation("com.madgag.spongycastle:prov:1.54.0.0")
    implementation("com.madgag.spongycastle:pkix:1.54.0.0")
    implementation("com.madgag.spongycastle:pg:1.54.0.0")

    // Room
    api("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    api("androidx.room:room-ktx:2.6.1")


    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}