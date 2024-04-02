plugins {
    id("ooo.android.library")
}

oddOneOut {
    flowroutines()
    firebase()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.network"
}

dependencies {
    implementation(projects.libraries.common)
}
