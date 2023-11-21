plugins {
    id("spyfall.android.library")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.kotlin.std)
    implementation(libs.kotlinx.coroutines)
}

android {
    namespace = "spyfallx.core.common"
}
