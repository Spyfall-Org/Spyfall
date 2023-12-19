plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.network"
}

dependencies {
    implementation(projects.libraries.common)
}
