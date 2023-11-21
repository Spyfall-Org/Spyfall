import com.spyfall.convention.util.getModule

plugins {
    id("spyfall.android.library")
}

spyfall {
    compose()
}

android {
    namespace = "com.dangerfield.spyfall.ui.internal"
}
dependencies {
    implementation(getModule("libraries:common"))
}
