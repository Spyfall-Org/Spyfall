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
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
