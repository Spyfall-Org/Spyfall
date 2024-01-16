plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.rules.internal"
}
dependencies {
    implementation(projects.features.rules)
    implementation(projects.libraries.common)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
}
