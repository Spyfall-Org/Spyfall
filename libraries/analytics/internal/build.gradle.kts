plugins {
    id("spyfall.android.library")
}

spyfall {
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
