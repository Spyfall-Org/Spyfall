plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.termofservice.internal"
}
dependencies {
    implementation(projects.features.termOfService)
    implementation(projects.libraries.common)
    implementation(projects.libraries.storage)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
