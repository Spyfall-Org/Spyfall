plugins {
    id("ooo.android.library")
    id("org.jetbrains.kotlin.kapt")
}

oddOneOut {
    flowroutines()
    daggerHilt()
    moshi()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.storage"
}

dependencies {
    implementation(projects.libraries.common)
    kapt(project.libs.room.compiler)

    api(libs.autoDagger.androidx.room)
    api(libs.androidx.datastore)
    api(libs.androidx.datastore.core)
    api(project.libs.room)
    api(libs.moshi)
    api(libs.kotlinx.serialization.json)
}
