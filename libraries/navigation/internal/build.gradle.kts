plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.navigation.internal"
}
dependencies {
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.common)
}
