plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.config"
}

dependencies {
    implementation(projects.libraries.common)
}
