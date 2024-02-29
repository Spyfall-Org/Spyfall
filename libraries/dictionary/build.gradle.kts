plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.dictionary"
}

dependencies {
    implementation(projects.libraries.common)
}
