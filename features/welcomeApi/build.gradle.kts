import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(getModule("libraries:core"))
}
