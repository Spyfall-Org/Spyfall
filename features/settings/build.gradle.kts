import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)

    // Dependency Injection
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(getModule("features:settingsApi"))
}
