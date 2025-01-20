import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

val description = "A kotlin coroutine based solution for handling in app purchases with the android billing library."

// Module
val artifactId = "library"
val androidNamespace = "com.michaelflisar.kotbilling"

// Library
val libraryName = "KotBilling"
val libraryDescription = "KotBilling - $artifactId module - $description"
val groupID = "io.github.mflisar.kotbilling"
val release = 2022
val github = "https://github.com/MFlisar/KotBilling"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Variables for Documentation Generator
// -------------------

// # DEP + GROUP are optional arrays!

// OPTIONAL = "false"               // defines if this module is optional or not
// GROUP_ID = "core"                // defines the "grouping" in the documentation this module belongs to
// DEP = "deps.billing|Play Billing|https://developer.android.com/google/play/billing/integrate"
// PLATFORM_INFO = ""               // defines a comment that will be shown in the documentation for this modules platform support

// GLOBAL DATA
// BRANCH = "master"        // defines the branch on github (master/main)
// GROUP = "core|Core|core"
// #GROUP = "modules|Modules|dialog modules"

// -------------------
// Setup
// -------------------

android {

    namespace = androidNamespace

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

mavenPublishing {

    configure(AndroidSingleVariantLibrary("release", true, true))

    coordinates(
        groupId = groupID,
        artifactId = artifactId,
        version = System.getenv("TAG")
    )

    pom {
        name.set(libraryName)
        description.set(libraryDescription)
        inceptionYear.set("$release")
        url.set(github)

        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
            }
        }

        developers {
            developer {
                id.set("mflisar")
                name.set("Michael Flisar")
                email.set("mflisar.development@gmail.com")
            }
        }

        scm {
            url.set(github)
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)

    // Enable GPG signing for all publications
    signAllPublications()
}