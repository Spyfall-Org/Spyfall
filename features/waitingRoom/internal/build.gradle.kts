plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    flowroutines()
    daggerHilt(withProcessors = true)
}

android {
    namespace = "com.dangerfield.spyfall.features.waitingroom.internal"
}
dependencies {
    implementation(projects.features.waitingRoom)
    implementation(projects.features.videoCall)
    implementation(projects.features.gamePlay)
    implementation(projects.features.ads.ui)
    implementation(projects.libraries.common)
    implementation(projects.libraries.game)
    implementation(projects.libraries.session)
    implementation(projects.libraries.analytics)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(libs.google.play.services.ads)
}
