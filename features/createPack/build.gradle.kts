plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.createpack"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
