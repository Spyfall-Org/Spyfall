plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    flowroutines()
}

android {
    namespace = "com.dangerfield.spyfall.features.ads.ui"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    api(projects.features.ads)
    implementation(libs.google.play.services.ads)
}
