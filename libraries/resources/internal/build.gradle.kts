plugins {
    id("ooo.android.library")
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.resources.internal"
}
dependencies {
    implementation(projects.libraries.resources)
    implementation(projects.libraries.common)
}
