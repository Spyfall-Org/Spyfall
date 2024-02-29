plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.waitingroom"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.features.welcome)
}
