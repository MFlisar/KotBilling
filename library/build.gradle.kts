plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

android {

    namespace = "com.michaelflisar.kotbilling"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    lint {
        abortOnError = false
    }
}

dependencies {

    // ------------------------
    // Kotlin
    // ------------------------

    implementation(libs.kotlin)

    // ------------------------
    // AndroidX / Google / Goolge
    // ------------------------

    implementation(androidx.core)
    implementation(androidx.lifecycle)
    implementation(androidx.startup)

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.billing)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "library"
                from(components["release"])
            }
        }
    }
}