
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.google.play.services)
        classpath(libs.firebase.crashlytics)
        classpath(libs.hilt.gradle)
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    id("spyfall.android.detekt")
    id("spyfall.android.checkstyle")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.fabric.io/public")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
