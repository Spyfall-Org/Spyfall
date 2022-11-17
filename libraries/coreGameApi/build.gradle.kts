import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
}

dependencies {
    implementation(getModule("libraries:core"))
}
