plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.common.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(libs.androidx.core)
}
