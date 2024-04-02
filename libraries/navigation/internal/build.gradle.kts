plugins {
    id("ooo.android.library")
}

oddOneOut {
    compose()
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.navigation.internal"
}
dependencies {
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.common)
    implementation(projects.libraries.resources)
    implementation(projects.libraries.ui)
    implementation(libs.browser)

}
