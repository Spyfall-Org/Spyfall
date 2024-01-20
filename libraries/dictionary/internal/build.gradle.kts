plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    flowroutines()
    firebase()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.dictionary.internal"
}
dependencies {
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.storage)
    implementation(libs.moshi)
    implementation(projects.libraries.common)
}
