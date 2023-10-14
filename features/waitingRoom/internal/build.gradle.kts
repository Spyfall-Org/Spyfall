plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom.internal"
}
dependencies {
    implementation(projects.features.waitingRoom)
    implementation(projects.libraries.coreCommon)
}
