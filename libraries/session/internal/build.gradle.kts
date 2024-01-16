plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    firebase()
    daggerHilt()
    moshi()
    storage()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.session.internal"
}
dependencies {
    implementation(projects.libraries.session)
    implementation(projects.libraries.session.storage)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
