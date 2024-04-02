plugins {
    id("ooo.android.library")
}

oddOneOut {
    room()
    daggerHilt()
    moshi()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.session.storage"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
}
