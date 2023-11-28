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
    namespace = "com.dangerfield.spyfall.libraries.config.internal"
}
dependencies {
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(projects.features.qa)
}
