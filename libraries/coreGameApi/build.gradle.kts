import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
