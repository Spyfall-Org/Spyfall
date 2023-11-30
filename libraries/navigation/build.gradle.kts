
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
    api(libs.androidx.navigation.compose)
    api(libs.hilt.navigation.compose)
}
