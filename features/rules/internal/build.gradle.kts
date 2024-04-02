plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.rules.internal"
}
dependencies {
    implementation(projects.features.rules)
    implementation(projects.features.gamePlay)
    implementation(projects.libraries.common)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
