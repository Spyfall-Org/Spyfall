plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt()
    compose()
    flowroutines()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.qa.internal"
}
dependencies {
    implementation(projects.features.qa)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
}
