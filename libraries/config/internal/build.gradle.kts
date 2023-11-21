plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    flowroutines()
    firebase()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.config.internal"
}
dependencies {
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.moshi)
    implementation(libs.androidx.datastore.core)
}
