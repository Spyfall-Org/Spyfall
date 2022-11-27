import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(getModule("libraries:core"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("features:settingsApi"))
}
