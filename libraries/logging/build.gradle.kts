plugins {
    id("spyfall.android.library")
}

spyfall {
    firebase()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.logging"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}
