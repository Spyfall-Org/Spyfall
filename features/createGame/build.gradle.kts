import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
}