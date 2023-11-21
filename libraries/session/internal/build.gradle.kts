plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.session.internal"
}
dependencies {
    implementation(projects.libraries.session)
    implementation(projects.libraries.common)
}
