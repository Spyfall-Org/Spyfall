plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.welcome.internal"
}
dependencies {
    implementation(projects.features.welcome)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.game)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.dictionary)
    implementation(projects.features.joinGame)
    implementation(projects.features.waitingRoom)
    implementation(projects.features.gamePlay)
    implementation(projects.features.settings)
    implementation(projects.features.newGame)
    implementation(projects.features.rules)
    implementation(projects.features.forcedUpdate)
    implementation(projects.libraries.navigation)
}
