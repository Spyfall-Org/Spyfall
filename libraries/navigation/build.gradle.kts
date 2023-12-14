
plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.navigation"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    api(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
