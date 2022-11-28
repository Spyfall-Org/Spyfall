import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(getModule("libraries:core"))
    implementation(getModule("libraries:coreUi"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreSpyfallGame"))
    implementation("androidx.core:core-ktx:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")
}
