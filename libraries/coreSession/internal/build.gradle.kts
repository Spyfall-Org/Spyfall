plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.coresession.internal"
}
dependencies {
    implementation(projects.libraries.coreSession)
    implementation(projects.libraries.coreCommon)
}
