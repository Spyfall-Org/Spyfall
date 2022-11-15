plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.javax.inject)
}
