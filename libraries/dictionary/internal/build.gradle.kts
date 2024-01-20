plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    flowroutines()
    firebase()
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.dictionary.internal"
}
dependencies {
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.navigation)
    implementation(libs.moshi)
    implementation(projects.libraries.common)
}
