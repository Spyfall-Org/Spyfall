import com.spyfall.convention.util.getModule

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
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
