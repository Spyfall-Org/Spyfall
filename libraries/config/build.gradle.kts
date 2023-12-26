plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
    moshi()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.config"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(libs.moshi)
}