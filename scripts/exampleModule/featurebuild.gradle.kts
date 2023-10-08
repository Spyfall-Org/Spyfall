import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(getModule("libraries:coreCommon"))
}
