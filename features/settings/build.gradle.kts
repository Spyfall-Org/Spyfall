plugins {
    id("spyfall.android.feature")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
android {
    namespace = "com.dangerfield.spyfall.settings"
}
