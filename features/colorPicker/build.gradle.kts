import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.colorpicker"
}
dependencies {
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
    implementation(libs.kotlinx.serialization.json)
}
