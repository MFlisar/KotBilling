dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }
        create("app") {
            from(files("gradle/app.versions.toml"))
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
