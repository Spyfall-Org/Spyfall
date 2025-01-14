plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.joingame.internal"
}
dependencies {
    implementation(projects.features.joinGame)
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.common)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.game)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.session)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
    testImplementation(projects.libraries.test)
}
