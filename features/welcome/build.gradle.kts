plugins {
    id("kotlin-android")
    id("com.android.library")
}

android {
    compileSdk = AppVersions.compileSdkVersion
    defaultConfig {
        minSdk = AppVersions.minSdkVersion
        targetSdk = AppVersions.targetSdkVersion
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

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)

    implementation(getModule("core"))
    implementation(getModule("coreGameApi"))
    implementation(getModule("coreUi"))
    implementation(getModule("features:welcomeApi"))
    implementation(getModule("features:settingsApi"))
}
