plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    firebase()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.game.internal"
}
dependencies {
    implementation(projects.libraries.game)
    implementation(projects.libraries.config)
    implementation(projects.libraries.common)
}
