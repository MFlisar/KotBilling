import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.michaelflisar.kmplibrary.BuildFilePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.kotbilling"

// -------------------
// Setup
// -------------------

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(buildFilePlugin.javaVersion()))
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

// -------------------
// Configurations
// -------------------

// android configuration
android {
    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )
}

// maven publish configuration
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish(
        platform = AndroidSingleVariantLibrary("release", true, true)
    )