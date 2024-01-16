
plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt()
    storage()
    compose()
}
android {
    namespace = "com.dangerfield.spyfall.features.colorpicker.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.analytics)
    implementation(projects.features.colorPicker)
    implementation(projects.libraries.flowroutines)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.resources)
    implementation(libs.lottie.compose)
    implementation(projects.libraries.navigation)
}
