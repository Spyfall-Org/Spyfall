plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.gameplay.internal"
}
dependencies {
    implementation(projects.features.gamePlay)
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
}
