
plugins {
    id("ooo.android.library")
}

oddOneOut {
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.navigation"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    api(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
