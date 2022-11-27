import com.spyfall.convention.shared.SpyfallConstants
import com.spyfall.convention.shared.buildConfigField
import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.application")
    id("spyfall.android.hilt")
    id("com.google.firebase.crashlytics")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
}

android {

    defaultConfig {
        applicationId = "com.dangerfield.spyfall.free"
        versionCode = SpyfallConstants.versionCode
        versionName = SpyfallConstants.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["screenOrientation"] = "unspecified"

        buildConfigField("VERSION_CODE", SpyfallConstants.versionCode)
        buildConfigField("VERSION_NAME", SpyfallConstants.versionName)
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

    androidExtensions {
        isExperimental = true
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

    // ad mob
    implementation(libs.google.play.services.ads)

    // firebase libraries
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

    // testing
    // Koin testing tools
    testImplementation(libs.koin.testing)
    // Needed JUnit version
    testImplementation(libs.koin.junit)

    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.roboelectric)
    testImplementation(libs.mockito)
    testImplementation(libs.androidx.test.arch.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.coroutines.test)
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreSpyfallGame"))
    implementation(getModule("libraries:core"))
    implementation(getModule("features:settingsApi"))
    implementation(getModule("features:settings"))
    implementation(getModule("features:welcome"))
    implementation(getModule("features:splash"))
}