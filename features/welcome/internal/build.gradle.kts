plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.welcome.internal"
}
dependencies {
    implementation(projects.features.welcome)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.libraries.coreUi)
    implementation(projects.libraries.coreCommon)
    implementation(projects.features.joinGame)
    implementation(projects.features.newGame)
}
