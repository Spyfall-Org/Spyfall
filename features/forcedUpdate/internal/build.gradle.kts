plugins {
    id("ooo.android.feature")
}

oddOneOut {
    firebase()
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.forcedupdate.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.analytics)
    implementation(projects.features.forcedUpdate)
    implementation(projects.libraries.navigation)
}