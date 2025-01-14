plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.ads.ui"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    api(projects.features.ads)
    implementation(libs.google.play.services.ads)
}
