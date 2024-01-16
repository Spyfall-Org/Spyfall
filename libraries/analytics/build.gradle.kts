plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.analytics"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
}
