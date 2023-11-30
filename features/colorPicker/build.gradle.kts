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
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.libraries.navigation)
}
