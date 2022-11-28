import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(getModule("features:settingsApi"))
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
}
