plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.colorpicker"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.libraries.navigation)
}
