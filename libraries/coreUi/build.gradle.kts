
plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.android")
}

spyfall {
    compose()
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
    implementation(projects.libraries.coreUi.internal)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
}
android {
    namespace = "spyfallx.coreui"
}
