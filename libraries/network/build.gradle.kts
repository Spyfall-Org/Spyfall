plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    firebase()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.network"
}

dependencies {
    implementation(projects.libraries.common)
}
