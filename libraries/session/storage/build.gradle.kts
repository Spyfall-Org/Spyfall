plugins {
    id("spyfall.android.library")
}

spyfall {
    room()
    daggerHilt()
    moshi()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.session.storage"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
}
