import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.library")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreApi"))
}
