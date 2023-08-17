import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.feature")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(getModule("features:settingsApi"))
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
