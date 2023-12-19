plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.features.videocall.internal"
}
dependencies {
    implementation(projects.features.videoCall)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
