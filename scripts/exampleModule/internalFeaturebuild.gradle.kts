plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.features.example)
    implementation(projects.libraries.common)
}
