plugins {
    id("spyfall.android.feature")
}

spyfall {
    firebase()
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.forcedupdate.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.features.forcedUpdate)
}