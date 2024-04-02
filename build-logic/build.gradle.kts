import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.dangerfield.oddoneoout.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

repositories {
    gradlePluginPortal()
    google()
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    api(libs.moshix.gradlePlugin)
    api(libs.ksp.gradlePlugin)
    implementation(libs.moshi)
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("com.google.gms:google-services:4.3.14")
    implementation("org.kohsuke:github-api:1.319")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "ooo.android.application"
            implementationClass = "com.spyfall.plugin.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "ooo.android.library"
            implementationClass = "com.spyfall.plugin.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "ooo.android.feature"
            implementationClass = "com.spyfall.plugin.AndroidFeatureConventionPlugin"
        }

        register("javaLibrary") {
            id = "ooo.java.library"
            implementationClass = "com.spyfall.plugin.JavaLibraryConventionPlugin"
        }

        register("androidDetekt") {
            id = "ooo.android.detekt"
            implementationClass = "com.spyfall.plugin.AndroidDetektConventionPlugin"
        }
    }
}
