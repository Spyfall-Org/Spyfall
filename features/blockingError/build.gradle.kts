plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.blockingerror"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
}
