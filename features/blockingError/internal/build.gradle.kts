plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt()
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.blockingerror.internal"
}
dependencies {
    implementation(projects.features.blockingError)
    implementation(projects.libraries.common)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
