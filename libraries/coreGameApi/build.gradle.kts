import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.android")
}

spyfall {
    flowroutines()
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
}
android {
    namespace = "spyfallx.coregameapi"
}
