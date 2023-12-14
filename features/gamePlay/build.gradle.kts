plugins {
    id("spyfall.android.feature")
}


android {
    namespace = "com.dangerfield.spyfall.features.gameplay"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.features.welcome)
}
