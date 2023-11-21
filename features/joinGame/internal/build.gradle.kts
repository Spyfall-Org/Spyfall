plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.features.joingame.internal"
}
dependencies {
    implementation(projects.features.joinGame)
    implementation(projects.libraries.common)
    implementation(projects.libraries.session)
    implementation(projects.libraries.ui)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
}
