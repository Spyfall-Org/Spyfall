import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreUi"))
}
android {
    namespace = "com.dangerfield.spyfall.creategame"
}
