plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.features.rules.internal"
}
dependencies {
    implementation(projects.features.rules)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
