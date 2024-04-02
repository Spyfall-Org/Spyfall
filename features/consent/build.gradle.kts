plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.consent"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
    implementation(libs.user.messaging.platform)
}
