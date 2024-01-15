plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.kapt")
}

spyfall {
    flowroutines()
    daggerHilt()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.storage"
}

dependencies {
    implementation(projects.libraries.common)
    kapt(project.libs.room.compiler)

    api(libs.autoDagger.androidx.room)
    api(libs.androidx.datastore)
    api(libs.androidx.datastore.core)
    api(project.libs.room)
    api(libs.kotlinx.serialization.json)
}
