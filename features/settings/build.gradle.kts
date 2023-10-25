plugins {
    id("spyfall.android.feature")
}
spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.settings"
}
dependencies {
    implementation(projects.libraries.coreCommon)
}
