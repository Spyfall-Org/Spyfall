plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.gameplay"
}
dependencies {
    implementation(projects.libraries.coreCommon)
}
