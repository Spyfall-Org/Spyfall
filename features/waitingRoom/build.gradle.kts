plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom"
}
dependencies {
    implementation(projects.libraries.coreCommon)
}
