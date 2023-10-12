plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.features.example)
    implementation(projects.libraries.coreCommon)
}
