plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.spyfall.features.settings.internal"
}
dependencies {
    implementation(projects.features.settings)
    implementation(projects.libraries.coreCommon)
}
