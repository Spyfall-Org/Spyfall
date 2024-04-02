plugins {
    id("ooo.android.library")
}

oddOneOut {
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.dictionary"
}

dependencies {
    implementation(projects.libraries.common)
}
