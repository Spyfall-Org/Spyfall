plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt(withProcessors = true)
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom.internal"
}
dependencies {
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
