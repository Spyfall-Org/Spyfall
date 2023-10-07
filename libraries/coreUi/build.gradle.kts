plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.android")
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
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
    implementation(libs.androidx.core.splashscreen)
}
android {
    namespace = "spyfallx.coreui"
}
