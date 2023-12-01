plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.navigation.internal"
}
dependencies {
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)

}
