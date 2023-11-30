plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt()
    compose()
}
android {
    namespace = "com.dangerfield.spyfall.features.qa.internal"
}
dependencies {
    implementation(projects.features.qa)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
}
