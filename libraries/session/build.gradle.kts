plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.session"
}

dependencies {
    implementation(projects.libraries.common)
}
