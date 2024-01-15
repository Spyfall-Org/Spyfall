plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    firebase()
    flowroutines()
    storage()
    moshi()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.game.internal"
}
dependencies {
    implementation(projects.libraries.game)
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
