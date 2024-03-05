
plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.android")
}

spyfall {
    compose()
    flowroutines()
    optIn("androidx.compose.material3.ExperimentalMaterial3Api")
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.javax.inject)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(projects.libraries.common)
    implementation(projects.features.ads)
    api(projects.libraries.dictionary)
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
}

android {
    namespace = "spyfallx.ui"
}
