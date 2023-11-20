plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt()
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.blockingerror.internal"
}
dependencies {
    implementation(projects.features.blockingError)
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
}
