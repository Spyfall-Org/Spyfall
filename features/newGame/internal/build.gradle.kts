plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.newgame.internal"
}
dependencies {
    implementation(projects.features.videoCall)
    implementation(projects.features.newGame)
    implementation(projects.features.waitingRoom)
    implementation(projects.features.gamePlay)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.config)
    implementation(projects.libraries.game)
    implementation(projects.libraries.ui)
    implementation(libs.lottie.compose)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.network)
    implementation(project(":features:createPack"))

    testImplementation(projects.libraries.test)
}
