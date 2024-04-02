plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.videocall.internal"
}
dependencies {
    implementation(projects.features.videoCall)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.game)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
