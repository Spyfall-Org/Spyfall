import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
}
android {
    namespace = "spyfallx.coreconfig"
}
