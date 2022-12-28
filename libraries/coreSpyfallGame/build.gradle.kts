import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.library")
    kotlin("plugin.parcelize")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines)
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreCommon"))
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
