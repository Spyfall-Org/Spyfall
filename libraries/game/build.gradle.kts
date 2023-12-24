plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    moshi()
    datastore()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.game"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
