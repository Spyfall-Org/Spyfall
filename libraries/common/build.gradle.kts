plugins {
    id("spyfall.android.library")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.kotlin.std)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.core)
}

android {
    namespace = "spyfallx.core.common"
}
