plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
    compose()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.flowroutines"
}

dependencies {
    api(project.libs.kotlinx.coroutines)
    implementation(projects.libraries.common)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}
