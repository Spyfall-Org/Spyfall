plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.resources"
}

dependencies {
    implementation(projects.libraries.common)
}
