import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreWerewolfGame"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("features:splash"))
}
