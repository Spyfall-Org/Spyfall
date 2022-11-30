plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
