plugins {
    id("spyfall.android.feature")
}

spyfall {
    daggerHilt(withProcessors = false)
}
dependencies {
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)
    
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreGameApi)
    implementation(projects.libraries.coreSpyfallGame)
    implementation(projects.features.splash)
}
android {
    namespace = "com.dangerfield.spyfall.splash.spyfall"
}
