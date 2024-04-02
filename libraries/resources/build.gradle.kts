plugins {
    id("ooo.android.library")
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.resources"
}

dependencies {
    implementation(projects.libraries.common)
}
