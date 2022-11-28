plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
}
