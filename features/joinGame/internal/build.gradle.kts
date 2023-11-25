plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.features.joingame.internal"
}
dependencies {
    implementation(projects.features.joinGame)
    implementation(projects.libraries.common)
    implementation(projects.libraries.game)
    implementation(projects.libraries.session)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
}
