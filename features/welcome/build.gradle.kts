import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(getModule("libraries:core"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("features:welcomeApi"))
    implementation(getModule("features:settingsApi"))
}
