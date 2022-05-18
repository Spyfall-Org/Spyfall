plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
    id("kotlin-kapt")

}

apply(from = "checkstyle.gradle")
apply(from = "detekt.gradle")

android {

    compileSdkVersion(31)
    defaultConfig {
        applicationId = "com.dangerfield.spyfall"
        minSdkVersion(21)
        targetSdkVersion(31)
        versionCode = 10
        versionName = "1.2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
        named("debug") {
            versionNameSuffix = "-DEBUG"
        }
    }

    flavorDimensions("version")
    productFlavors {
        create("free") {
            applicationId = "com.dangerfield.spyfall.free"
            versionNameSuffix = "-free"
            dimension = "version"
        }
        create("paid") {
            applicationId = "com.dangerfield.spyfall.paid"
            versionNameSuffix = "-paid"
            dimension = "version"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }

    androidExtensions {
        isExperimental = true
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.0-alpha03")
    implementation("androidx.core:core-ktx:1.9.0-alpha03")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-beta01")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.0-alpha03")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha06")
    implementation("androidx.recyclerview:recyclerview:1.3.0-alpha02")
    implementation("android.arch.navigation:navigation-fragment:1.0.0")
    implementation("android.arch.navigation:navigation-ui:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")

    //ad mob
    implementation("com.google.android.gms:play-services-ads:20.6.0")

    //firebase libraries
    implementation("com.google.firebase:firebase-database:20.0.4")
    implementation("com.google.firebase:firebase-firestore:24.1.1")
    implementation("com.google.firebase:firebase-storage:20.0.1")

    //that sweet sweet kotlin coroutines library
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.1.1")

    //lottie for animations
    implementation("com.airbnb.android:lottie:3.7.0")

    //Dependency Injection
    implementation("io.insert-koin:koin-core:3.1.6")
    implementation("io.insert-koin:koin-android:3.1.6")

    implementation("com.google.dagger:hilt-android:2.40.5")
    kapt("com.google.dagger:hilt-android-compiler:2.40.5")

    //testing
    // Koin testing tools
    testImplementation("io.insert-koin:koin-test:3.1.6")
    // Needed JUnit version
    testImplementation("io.insert-koin:koin-test-junit4:3.1.6")

    testImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("org.robolectric:robolectric:4.6.1")
    testImplementation("org.mockito:mockito-core:3.9.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}
