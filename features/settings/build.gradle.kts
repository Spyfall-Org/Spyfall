plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.settings"
}
dependencies {
    implementation(projects.libraries.coreCommon)
}
