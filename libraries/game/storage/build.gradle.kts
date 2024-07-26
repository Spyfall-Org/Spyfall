plugins {
    id("ooo.android.library")
}

oddOneOut {
    storage()
}

android {
    namespace = "com.dangerfield.oddoneoout.libraries.game.storage"
}

dependencies {
    implementation(projects.libraries.common)
}
