plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
    flowroutines()
    firebase()
}

android {
    namespace = "com.dangerfield.spyfall.features.settings.internal"
}
dependencies {
    implementation(projects.features.settings)
    implementation(projects.features.qa)
    implementation(projects.features.colorPicker)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(libs.lottie.compose)
}
