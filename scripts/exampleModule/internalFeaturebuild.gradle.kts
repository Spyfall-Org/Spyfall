plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.features.example)
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
