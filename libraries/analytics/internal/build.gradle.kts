plugins {
    id("ooo.android.library")
}

oddOneOut {
    daggerHilt()
    firebase()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.analytics.internal"
}
dependencies {
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.common)
}
