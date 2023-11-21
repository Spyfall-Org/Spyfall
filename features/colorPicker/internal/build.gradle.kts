
plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt(withProcessors = false)
}
android {
    namespace = "com.dangerfield.spyfall.features.colorpicker.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.features.colorPicker)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.libraries.flowroutines)
    implementation(projects.libraries.ui)
}
