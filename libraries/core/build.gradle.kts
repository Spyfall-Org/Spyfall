plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
    kotlin("plugin.parcelize")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
}
