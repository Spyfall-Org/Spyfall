plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.network"
}

dependencies {
    implementation(projects.libraries.common)
}
