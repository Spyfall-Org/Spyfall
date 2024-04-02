plugins {
    id("ooo.android.library")
}

oddOneOut {
    daggerHilt()
    flowroutines()
    firebase()
    compose()
    moshi()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.dictionary.internal"
}
dependencies {
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.config)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.common)
}
