plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.gameplay.internal"
}
dependencies {
    implementation(projects.features.videoCall)
    implementation(projects.libraries.game)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.dictionary)
    implementation(projects.features.gamePlay)
    implementation(projects.features.blockingError)
    implementation(projects.features.ads)
    implementation(projects.features.ads.ui)
    implementation(projects.features.welcome)
    implementation(projects.features.rules)
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.session)
}
