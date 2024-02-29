plugins {
    id("spyfall.android.feature")
}
spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.settings"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
}
