plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.datastore"
}

dependencies {
    implementation(projects.libraries.common)
    api(libs.androidx.datastore)
    api(libs.androidx.datastore.core)
    api(libs.kotlinx.serialization.json)
}
