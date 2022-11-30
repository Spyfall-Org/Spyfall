import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
}
