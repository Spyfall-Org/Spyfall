plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.common.internal"
}
dependencies {
    implementation(projects.libraries.common)
    implementation(libs.androidx.core)
}
