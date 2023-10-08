plugins {
    id("spyfall.android.library")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}

android {
    namespace = "spyfallx.core"
}
