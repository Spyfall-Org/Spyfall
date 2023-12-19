plugins {
    id("spyfall.android.application")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

spyfall {
    compose()
    flowroutines()
    daggerHilt()
    firebase()
}

android {

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }

    namespace = "com.dangerfield.spyfall"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support)
    implementation(libs.androidx.lifecycle.ext)
    implementation(libs.androidx.lifecycle.vm)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.std)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.arch.fragment.navigation)
    implementation(libs.arch.navigation.ui)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(project.libs.dagger)
    implementation(project.libs.dagger.hilt.android)
    kapt(project.libs.dagger.hilt.android.compiler)

    // ad mob
    implementation(libs.google.play.services.ads)

    // firebase libraries
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)

    //  that sweet sweet kotlin coroutines library
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.navigation.compose)

    /**
     * Project Modules
     * Both internal and external should be included here to make sure all objects
     * are added to the dependency graph
     */
    implementation(projects.features.forcedUpdate)
    implementation(projects.features.forcedUpdate.internal)
    implementation(projects.features.colorPicker)
    implementation(projects.features.colorPicker.internal)
    implementation(projects.features.welcome)
    implementation(projects.features.welcome.internal)
    implementation(projects.features.joinGame)
    implementation(projects.features.joinGame.internal)
    implementation(projects.features.gamePlay)
    implementation(projects.features.gamePlay.internal)
    implementation(projects.features.settings)
    implementation(projects.features.settings.internal)
    implementation(projects.features.blockingError)
    implementation(projects.features.blockingError.internal)
    implementation(projects.features.newGame)
    implementation(projects.features.newGame.internal)
    implementation(projects.features.waitingRoom)
    implementation(projects.features.waitingRoom.internal)
    implementation(projects.libraries.datastore)
    implementation(projects.libraries.config)
    implementation(projects.libraries.config.internal)
    implementation(projects.libraries.config.internal)
    implementation(projects.libraries.logging)
    implementation(projects.libraries.common)
    implementation(projects.libraries.common.internal)
    implementation(projects.libraries.ui)
    implementation(projects.libraries.ui.internal)
    implementation(projects.libraries.session)
    implementation(projects.libraries.session.internal)
    implementation(projects.libraries.game)
    implementation(projects.libraries.game.internal)
    implementation(projects.features.qa)
    implementation(projects.features.qa.internal)
	implementation(projects.libraries.resources)
	implementation(projects.libraries.resources.internal)
	implementation(projects.features.rules)
	implementation(projects.features.rules.internal)
	implementation(projects.libraries.navigation)
	implementation(projects.libraries.navigation.internal)
	implementation(projects.features.videoCall)
	implementation(projects.features.videoCall.internal)
	implementation(projects.libraries.network)
	implementation(projects.libraries.network.internal)
    // STOP PROJECT MODULES (keep this line at the end of the project modules, used by ./create_module)

    // lottie for animations
    implementation(libs.lottie)

    // Dependency Injection
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.androidx.activity.compose)

    implementation(libs.moshi)

    // testing
    // Koin testing tools
    testImplementation(libs.koin.testing)
    // Needed JUnit version
    testImplementation(libs.koin.junit)

    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.roboelectric)
    testImplementation(libs.mockito)
    testImplementation(libs.androidx.test.arch.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
}