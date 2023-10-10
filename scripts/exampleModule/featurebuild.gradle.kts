plugins {
    id("spyfall.android.feature")
}

android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.libraries.coreCommon)
}
