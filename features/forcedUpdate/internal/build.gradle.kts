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
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.features.forcedUpdate)
}