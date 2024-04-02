plugins {
    id("ooo.android.library")
}

android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.libraries.example)
    implementation(projects.libraries.common)
}
