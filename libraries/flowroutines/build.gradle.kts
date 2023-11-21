plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.flowroutines"
}

dependencies {
    api(project.libs.kotlinx.coroutines)
    implementation(projects.libraries.common)
}
