import extension.script

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
        classpath (libs.hilt.gradle)
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
    plugin.detekt
    plugin.checkstyle
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