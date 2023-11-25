plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.game"
}

dependencies {
    implementation(projects.libraries.common)
}
