plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.qa"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
}
