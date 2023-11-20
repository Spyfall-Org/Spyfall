plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.corecommon.internal"
}
dependencies {
    implementation(projects.libraries.coreCommon)
    implementation(libs.androidx.core)
}
