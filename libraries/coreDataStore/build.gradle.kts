import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.coredatastore"
}

dependencies {
    implementation(projects.libraries.coreCommon)
}
