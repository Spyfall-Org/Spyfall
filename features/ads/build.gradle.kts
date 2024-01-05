plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.ads"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(libs.google.play.services.ads)
    implementation(project(":libraries:flowroutines"))
}
