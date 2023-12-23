plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.navigation.internal"
}
dependencies {
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.common)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.ui)
    implementation(libs.browser)

}
