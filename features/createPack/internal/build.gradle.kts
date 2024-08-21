plugins {
    id("ooo.android.feature")
}

oddOneOut {
    compose()
    daggerHilt()
}
android {
    namespace = "com.dangerfield.oddoneoout.features.createpack.internal"
}
dependencies {
    implementation(projects.features.createPack)
    implementation(projects.libraries.common)
    implementation(projects.libraries.navigation)
    implementation(projects.libraries.ui)
    implementation(project(":libraries:game"))
    implementation(project(":libraries:analytics"))
    implementation(project(":libraries:flowroutines"))
    implementation(project(":libraries:config"))
    implementation(project(":features:newGame"))
}
