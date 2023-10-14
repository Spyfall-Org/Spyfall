plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.gameplay.internal"
}
dependencies {
    implementation(projects.features.gamePlay)
    implementation(projects.libraries.coreCommon)
}
