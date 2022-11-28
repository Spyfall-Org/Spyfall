
import com.spyfall.convention.shared.WerewolfConstants
import com.spyfall.convention.shared.buildConfigField
import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.application")
    id("spyfall.android.hilt")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dangerfield.werewolf"

    defaultConfig {
        applicationId = "com.dangerfield.werewolf"
        versionCode = WerewolfConstants.versionCode
        versionName = WerewolfConstants.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("VERSION_CODE", WerewolfConstants.versionCode)
        buildConfigField("VERSION_NAME", WerewolfConstants.versionName)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support)

    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.arch.fragment.navigation)
    implementation(libs.arch.navigation.ui)
    implementation(libs.androidx.fragment.ktx)

    // firebase libraries
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation(getModule("libraries:coreUi"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreSpyfallGame"))
    implementation(getModule("libraries:core"))
    implementation(getModule("features:settingsApi"))
    implementation(getModule("features:settings"))
    implementation(getModule("features:welcome"))
    implementation(getModule("features:splash"))
}
