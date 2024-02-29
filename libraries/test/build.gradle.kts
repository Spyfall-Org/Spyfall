plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.test"
}

dependencies {
    implementation(projects.libraries.common)
}

dependencies {
    // WARNING: Must use implementation here to expose dependencies in this modules main dir.
    implementation(projects.libraries.common)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.test.junit)
    implementation(libs.androidx.datastore)

    // api to expose these dependencies to dependants
    api(libs.mockk)
    api(libs.robolectric)
    api(libs.assertk)
    api(libs.junit)
    api(libs.androidx.test.core)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)
}