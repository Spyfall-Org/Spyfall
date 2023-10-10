plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.welcome.internal"
}
dependencies {
    implementation(projects.features.welcome)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.libraries.coreUi)
}
