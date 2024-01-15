plugins {
    id("spyfall.android.library")
    id("org.jetbrains.kotlin.kapt")
}


spyfall {
    daggerHilt()
    moshi()
}

android {
    namespace = "com.dangerfield.spyfall.libraries.storage.internal"
}

dependencies {
    implementation(projects.libraries.storage)
    implementation(projects.libraries.common)

    implementation(project.libs.room)
    implementation(libs.room.common)
    kapt(project.libs.room.compiler)

    implementation(libs.autoDagger.androidx.room)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.libraries.session.storage)
    implementation(projects.libraries.session)
}
