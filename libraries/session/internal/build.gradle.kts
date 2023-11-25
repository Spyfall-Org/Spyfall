plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.session.internal"
}
dependencies {
    implementation(projects.libraries.session)
    implementation(projects.libraries.common)
}
