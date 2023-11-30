plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.welcome"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
