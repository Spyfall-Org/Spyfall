plugins {
    id("ooo.android.library")
}

oddOneOut {
    flowroutines()
    moshi()
    storage()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.game"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
