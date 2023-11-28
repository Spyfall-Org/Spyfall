plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.resources.internal"
}
dependencies {
    implementation(projects.libraries.resources)
    implementation(projects.libraries.common)
}
