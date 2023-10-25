plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.settings.internal"
}
dependencies {
    implementation(projects.features.settings)
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
}
