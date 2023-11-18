plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.coregame"
}

dependencies {
    implementation(projects.libraries.coreCommon)
}
