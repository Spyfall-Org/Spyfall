
plugins {
    `kotlin-dsl`
}

group = "com.dangerfield.spyfall.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.detekt.get()}")
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("com.google.gms:google-services:4.3.14")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "spyfall.android.application"
            implementationClass = "com.spyfall.convention.plugin.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "spyfall.android.library"
            implementationClass = "com.spyfall.convention.plugin.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "spyfall.android.feature"
            implementationClass = "com.spyfall.convention.plugin.AndroidFeatureConventionPlugin"
        }

        register("androidTest") {
            id = "spyfall.android.test"
            implementationClass = "com.spyfall.convention.plugin.AndroidTestConventionPlugin"
        }
        register("androidHilt") {
            id = "spyfall.android.hilt"
            implementationClass = "com.spyfall.convention.plugin.AndroidHiltConventionPlugin"
        }

        register("androidDetekt") {
            id = "spyfall.android.detekt"
            implementationClass = "com.spyfall.convention.plugin.AndroidDetektConventionPlugin"
        }

        register("androidCheckstyle") {
            id = "spyfall.android.checkstyle"
            implementationClass = "com.spyfall.convention.plugin.AndroidCheckstyleConventionPlugin"
        }
    }
}
