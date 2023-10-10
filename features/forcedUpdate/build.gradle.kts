plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.forcedupdate"
}
dependencies {
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
    implementation(libs.androidx.navigation.compose)
}
