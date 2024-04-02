import com.spyfall.util.getModule

plugins {
    id("ooo.android.library")
}

oddOneOut {
    compose()
}

android {
    namespace = "com.dangerfield.oddoneoout.ui.internal"
}
dependencies {
    implementation(getModule("libraries:common"))
}
