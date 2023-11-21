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
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(libs.lottie.compose)
}
