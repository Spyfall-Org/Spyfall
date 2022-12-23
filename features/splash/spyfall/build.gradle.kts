import com.spyfall.convention.shared.getModule

plugins {
    id("spyfall.android.feature")
}

dependencies {
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)
    
    implementation(getModule("libraries:coreCommon"))
    implementation(getModule("libraries:coreGameApi"))
    implementation(getModule("libraries:coreSpyfallGame"))
    implementation(getModule("features:splash"))
}