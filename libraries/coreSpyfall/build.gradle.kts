import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreApi"))
}
android {
    namespace = "spyfallx.corespyfall"
}
