plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.test.internal"
}
dependencies {
    implementation(projects.libraries.test)
    implementation(projects.libraries.common)
}
