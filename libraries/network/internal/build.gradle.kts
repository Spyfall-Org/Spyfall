plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
    firebase()
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.network.internal"
}
dependencies {
    implementation(projects.libraries.ui)
    implementation(projects.libraries.network)
    implementation(projects.libraries.common)
}
