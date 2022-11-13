plugins {
    id("kotlin-android")
    id("com.android.library")
    id("kotlin-android-extensions")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
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

    androidExtensions {
        isExperimental = true
    }
}


dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

}
