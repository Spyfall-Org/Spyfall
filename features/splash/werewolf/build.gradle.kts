import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreWerewolfGame"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("features:splash"))
}
