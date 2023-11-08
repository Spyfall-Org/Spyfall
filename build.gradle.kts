
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.google.services)
        classpath(libs.kotlinpoet)
        classpath(libs.firebase.crashlytics)
        classpath(libs.dagger.hilt.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    id("spyfall.android.detekt")
    id("spyfall.android.checkstyle")
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlinAndroid) apply false
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


