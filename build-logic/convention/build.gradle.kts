import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}


group = "com.dangerfield.spyfall.buildlogic"

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
    compileOnly(libs.ksp.gradlePlugin)
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("com.google.gms:google-services:4.3.14")
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "spyfall.android.application"
            implementationClass = "com.spyfall.convention.plugin.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "spyfall.android.library"
            implementationClass = "com.spyfall.convention.plugin.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "spyfall.android.feature"
            implementationClass = "com.spyfall.convention.plugin.AndroidFeatureConventionPlugin"
        }

        register("androidDetekt") {
            id = "spyfall.android.detekt"
            implementationClass = "com.spyfall.convention.plugin.AndroidDetektConventionPlugin"
        }

        register("androidCheckstyle") {
            id = "spyfall.android.checkstyle"
            implementationClass = "com.spyfall.convention.plugin.AndroidCheckstyleConventionPlugin"
        }
    }
}
