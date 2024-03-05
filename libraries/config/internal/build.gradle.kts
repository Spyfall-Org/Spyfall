plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    flowroutines()
    firebase()
    moshi()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.config.internal"
}
dependencies {
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
    implementation(projects.libraries.storage)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(projects.features.qa)
}
