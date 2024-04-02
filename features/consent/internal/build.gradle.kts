plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.consent.internal"
}

dependencies {
    implementation(projects.features.consent)
    implementation(projects.features.ads)
    implementation(projects.features.blockingError)
    implementation(projects.features.ads.ui)
    implementation(libs.google.play.services.ads)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
    implementation(libs.user.messaging.platform)
}
