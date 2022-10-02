plugins {
    id("kotlin-android")
    id("com.android.application")
}

android {

    compileSdk = AppVersions.compileSdkVersion
    defaultConfig {
        applicationId = "com.dangerfield.spyfall"
        minSdk = AppVersions.minSdkVersion
        targetSdk = AppVersions.targetSdkVersion
        versionCode = AppVersions.versionCode
        versionName = AppVersions.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragmnet)

}