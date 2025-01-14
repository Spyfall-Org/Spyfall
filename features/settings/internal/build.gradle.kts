plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    flowroutines()
    firebase()
    moshi()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.settings.internal"
}
dependencies {
    implementation(projects.features.settings)
    implementation(projects.features.blockingError)
    implementation(projects.features.qa)
    implementation(projects.features.colorPicker)
    implementation(projects.features.blockingError)
    implementation(projects.features.consent)
    implementation(projects.libraries.common)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.network)
    implementation(projects.libraries.config)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.session)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(libs.lottie.compose)
}
