plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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
        manifestPlaceholders["screenOrientation"] = "unspecified"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        named("debug") {
            versionNameSuffix = "-DEBUG"
        }

        create("legacy") {
            manifestPlaceholders["screenOrientation"] = "portrait"
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-LEGACY"
            matchingFallbacks += "debug"
            matchingFallbacks += "release"
        }
    }

    flavorDimensions("version")
    productFlavors {
        create("free") {
            applicationId = "com.dangerfield.spyfall.free"
            versionNameSuffix = "-free"
            dimension = "version"
        }
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

    kapt {
        correctErrorTypes = true
    }

    packagingOptions {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support)

    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.arch.fragment.navigation)
    implementation(libs.arch.navigation.ui)
    implementation(libs.androidx.fragment.ktx)

    //ad mob
    implementation(libs.google.play.services)

    //firebase libraries
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    //  that sweet sweet kotlin coroutines library
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.play.servies)

    // lottie for animations
    implementation(libs.lottie)

    // Dependency Injection
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //testing
    // Koin testing tools
    testImplementation(libs.koin.testing)
    //Needed JUnit version
    testImplementation(libs.koin.junit)

    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.roboelectric)
    testImplementation(libs.mockito)
    testImplementation(libs.androidx.test.arch.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)

    implementation(getModule("coreUi"))
    implementation(getModule("coreGameApi"))
    implementation(getModule("coreGame"))
    implementation(getModule("core"))
    implementation(getModule(":features:settingsApi"))
    implementation(getModule(":features:settings"))
    implementation(getModule(":features:welcome"))
    implementation(getModule(":features:welcomeApi"))

}
