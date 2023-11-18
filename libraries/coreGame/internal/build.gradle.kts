plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.coregame.internal"
}
dependencies {
    implementation(projects.libraries.coreGame)
    implementation(projects.libraries.coreCommon)
}
