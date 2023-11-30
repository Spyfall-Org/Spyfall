plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.features.newgame.internal"
}
dependencies {
    implementation(projects.features.newGame)
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.game)
    implementation(projects.libraries.ui)
    implementation(libs.lottie.compose)
    implementation(projects.libraries.navigation)
}
