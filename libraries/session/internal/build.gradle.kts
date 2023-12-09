plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    firebase()
    daggerHilt()
    moshi()
    datastore()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.session.internal"
}
dependencies {
    implementation(projects.libraries.session)
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)

}
