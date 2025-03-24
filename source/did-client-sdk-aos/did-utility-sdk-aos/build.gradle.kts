plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.omnione.did.sdk.utility"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

val customVersionMajorMinorBuild = "1.0.0"

tasks.register<Copy>("exportJar") {
    from("build/intermediates/runtime_library_classes_jar/debug/classes.jar")
    into("../release")
    include("classes.jar")
    rename("classes.jar", "${project.name}-${customVersionMajorMinorBuild}.jar")
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.bitcoinj:bitcoinj-core:0.15.7")
    implementation("com.madgag.spongycastle:core:1.54.0.0")
    implementation("com.madgag.spongycastle:prov:1.54.0.0")
    implementation("com.madgag.spongycastle:pkix:1.54.0.0")
    implementation("com.madgag.spongycastle:pg:1.54.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}