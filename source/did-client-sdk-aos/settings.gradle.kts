pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "did-client-sdk-aos"

include(":did-wallet-sdk-aos")
include(":did-communication-sdk-aos")
include(":did-core-sdk-aos")
include(":did-datamodel-sdk-aos")
include(":did-utility-sdk-aos")
