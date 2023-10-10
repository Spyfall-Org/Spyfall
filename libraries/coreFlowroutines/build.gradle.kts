plugins {
    id("spyfall.android.library")
}

spyfall {
    daggerHilt(withProcessors = false)
}
android {
    namespace = "com.dangerfield.spyfall.libraries.coreflowroutines"
}

dependencies {
    api(project.libs.kotlinx.coroutines)
}
