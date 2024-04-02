plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    flowroutines()
    daggerHilt(withProcessors = true)
}

android {
    namespace = "com.dangerfield.oddoneoout.features.waitingroom.internal"
}
dependencies {
    implementation(projects.features.waitingRoom)
    implementation(projects.features.videoCall)
    implementation(projects.features.gamePlay)
    implementation(projects.features.welcome)
    implementation(projects.features.rules)
    implementation(projects.features.ads.ui)
    implementation(projects.libraries.common)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.game)
    implementation(projects.libraries.session)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(libs.google.play.services.ads)
}
