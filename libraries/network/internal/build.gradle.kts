plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.network.internal"
}
dependencies {
    implementation(projects.libraries.network)
    implementation(projects.libraries.common)
}
