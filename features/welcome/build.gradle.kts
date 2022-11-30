import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("features:settingsApi"))
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
}
