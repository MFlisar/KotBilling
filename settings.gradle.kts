//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {

        // TOML Files
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }

        // Rest
        val kotlin = "1.7.20"
        create("tools") {
            version("kotlin", kotlin)
            version("gradle", "7.2.2")
            version("maven", "2.0")
        }
        create("app") {
            version("compileSdk", "32")
            version("minSdk", "21")
            version("targetSdk", "32")
        }
        create("libs") {
            library("kotlin", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
        }
    }
}

// --------------
// App
// --------------

include(":library")
project(":library").projectDir = file("library")

include(":demo")
project(":demo").projectDir = file("demo")
