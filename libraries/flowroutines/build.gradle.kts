plugins {
    id("ooo.android.library")
}

oddOneOut {
    daggerHilt()
    compose()
}
android {
    namespace = "com.dangerfield.oddoneoout.libraries.flowroutines"
}

dependencies {
    api(project.libs.kotlinx.coroutines)
    implementation(projects.libraries.common)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}
