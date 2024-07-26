plugins {
    id("ooo.android.library")
}

oddOneOut {
    daggerHilt()
    firebase()
    flowroutines()
    storage()
    moshi()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.game.internal"
}
dependencies {
    implementation(projects.libraries.game)
    implementation(projects.libraries.game.storage)
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
    implementation(projects.libraries.dictionary)
    implementation(projects.libraries.network)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.session)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
}
