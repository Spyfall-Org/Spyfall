plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.features.welcome.internal"
}
dependencies {
    implementation(projects.features.welcome)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.common)
    implementation(projects.libraries.resources)
    implementation(projects.features.joinGame)
    implementation(projects.features.settings)
    implementation(projects.features.newGame)
    implementation(projects.features.forcedUpdate)
    implementation(projects.libraries.navigation)
}
