plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.welcome"
}
dependencies {
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
    implementation(libs.androidx.navigation.compose)
    // TODO consider a navigation library that exposes exts and deps and such
}
