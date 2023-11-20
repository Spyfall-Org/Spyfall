plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt()
}
android {
    namespace = "com.dangerfield.spyfall.libraries.coreflowroutines"
}

dependencies {
    api(project.libs.kotlinx.coroutines)
    implementation(projects.libraries.coreCommon)
}
