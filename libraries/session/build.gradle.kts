plugins {
    id("spyfall.android.library")
}

spyfall {
    flowroutines()
    moshi()

}
android {
    namespace = "com.dangerfield.spyfall.libraries.session"
}

dependencies {
    implementation(projects.libraries.common)
    implementation(project.libs.kotlinx.serialization.json)
}
