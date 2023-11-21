plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom"
}
dependencies {
    implementation(projects.libraries.common)
}
