plugins {
    id("ooo.android.library")
}

oddOneOut {
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
