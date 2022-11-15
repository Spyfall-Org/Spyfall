import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.library")
    id("spyfall.android.hilt")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(getModule("libraries:core"))
}
