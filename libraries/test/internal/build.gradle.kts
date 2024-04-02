plugins {
    id("ooo.android.library")
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.test.internal"
}
dependencies {
    implementation(projects.libraries.test)
    implementation(projects.libraries.common)
}
