import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.application")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

spyfall {
    daggerHilt(true)
    daggerAndroid()
    compose()
}

android {

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }
    
    packagingOptions {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
    namespace = "com.dangerfield.spyfall"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support)

    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
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
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)

    // lottie for animations
    implementation(libs.lottie)

    // Dependency Injection
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)


    implementation(libs.androidx.activity.compose)
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
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreSpyfallGame"))
    implementation(getModule("libraries:coreCommon"))
}
