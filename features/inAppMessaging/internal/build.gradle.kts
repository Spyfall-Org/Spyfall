plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
    storage()
    flowroutines()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.inappmessaging.internal"
}
dependencies {
    implementation(projects.features.inAppMessaging)
    implementation(projects.libraries.common)
    implementation(projects.libraries.config)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
    implementation(libs.android.play.app.update)
    implementation(libs.android.play.app.update.ktx)

    testImplementation(projects.libraries.test)
}
