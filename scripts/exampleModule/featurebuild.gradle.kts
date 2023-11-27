plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.example"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
