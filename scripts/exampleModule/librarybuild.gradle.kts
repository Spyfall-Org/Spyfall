plugins {
    id("ooo.android.library")
}

android {
    namespace = "com.dangerfield.example"
}

dependencies {
    implementation(projects.libraries.common)
}
