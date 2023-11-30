plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    flowroutines()
    daggerHilt(withProcessors = true)
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom.internal"
}
dependencies {
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
