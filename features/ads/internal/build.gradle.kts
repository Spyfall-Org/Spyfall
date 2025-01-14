plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.ads.internal"
}
dependencies {
    implementation(projects.features.ads)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
