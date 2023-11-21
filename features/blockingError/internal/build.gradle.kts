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
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
