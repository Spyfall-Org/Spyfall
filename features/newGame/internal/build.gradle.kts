plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.newgame.internal"
}
dependencies {
    implementation(projects.features.newGame)
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
    implementation(libs.lottie.compose)
}
