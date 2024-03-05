plugins {
    id("spyfall.android.feature")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.features.inappmessaging"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.navigation)
    implementation(libs.android.play.app.update)
    implementation(libs.android.play.app.update.ktx)
}
