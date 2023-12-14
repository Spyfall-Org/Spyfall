plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.features.gameplay.internal"
}
dependencies {
    implementation(projects.libraries.game)
    implementation(projects.features.gamePlay)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(project(":libraries:session"))
}
