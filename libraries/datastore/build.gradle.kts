import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
}

android {
    namespace = "com.dangerfield.spyfall.libraries.datastore"
}

dependencies {
    implementation(projects.libraries.common)
}
