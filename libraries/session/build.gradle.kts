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
    implementation(projects.libraries.ui)
    implementation(project.libs.kotlinx.serialization.json)
}
