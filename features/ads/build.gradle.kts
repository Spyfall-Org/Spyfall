plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.oddoneout.features.ads"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(libs.google.play.services.ads)
    implementation(project(":libraries:flowroutines"))
}
