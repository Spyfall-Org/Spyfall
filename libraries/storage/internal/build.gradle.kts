plugins {
    id("ooo.android.library")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.room")
}

val schemasDir = layout.projectDirectory.dir("schemas")

room {
    schemaDirectory(schemasDir.toString())
}

oddOneOut {
    daggerHilt()
    moshi()
    flowroutines()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.storage.internal"
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

    implementation(projects.libraries.game.storage)
    implementation(projects.libraries.game)
}
