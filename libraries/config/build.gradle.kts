plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.config"
}

dependencies {
    implementation(projects.libraries.common)
}
