import com.spyfall.convention.util.getModule

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
    implementation(projects.libraries.coreCommon)
    implementation(projects.features.colorPicker)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.libraries.coreFlowroutines)
    implementation(projects.libraries.coreUi)
}
