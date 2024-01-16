plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.videocall"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
