plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    moshi()
    storage()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.game"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
