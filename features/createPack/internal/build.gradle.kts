plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.createpack.internal"
}
dependencies {
    implementation(projects.features.createPack)
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
}
