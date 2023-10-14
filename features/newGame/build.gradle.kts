plugins {
    id("spyfall.android.feature")
}
spyfall {
    compose()
}
android {
    namespace = "com.dangerfield.spyfall.features.newgame"
}
dependencies {
    implementation(projects.libraries.coreCommon)
    implementation(projects.libraries.coreUi)
}
