plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.coresession"
}

dependencies {
    implementation(projects.libraries.coreCommon)
}
