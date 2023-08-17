package com.spyfall.convention.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.spyfall.convention.util.BuildEnvironment
import com.spyfall.convention.util.SharedConstants
import com.spyfall.convention.util.buildConfigField
import com.spyfall.convention.util.configureGitHooksCheck
import com.spyfall.convention.util.configureKotlinAndroid
import com.spyfall.convention.util.checkForAppModuleSecretFiles
import com.spyfall.convention.util.getPackageName
import com.spyfall.convention.util.getVersionCode
import com.spyfall.convention.util.getVersionName
import com.spyfall.convention.util.loadAppProperty
import com.spyfall.convention.util.loadGradleProperty
import com.spyfall.convention.util.printDebugSigningWarningIfNeeded
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {

                configureKotlinAndroid(this)

                defaultConfig.apply {
                    targetSdk = SharedConstants.targetSdk
                    versionName = getVersionName()
                    versionCode = getVersionCode()
                    applicationId = getPackageName()
                    buildConfigField("VERSION_CODE", versionCode)
                    buildConfigField("VERSION_NAME", versionName)
                    buildConfigField("CONFIG_COLLECTION_KEY", loadAppProperty("configCollectionKey"))
                }

                project.afterEvaluate {
                    tasks.getByName("assembleRelease") {
                        doFirst { printDebugSigningWarningIfNeeded() }
                        doLast { printDebugSigningWarningIfNeeded() }
                    }

                    tasks.getByName("bundleRelease") {
                        doFirst { printDebugSigningWarningIfNeeded() }
                        doLast { printDebugSigningWarningIfNeeded() }
                    }
                }

                buildTypes.forEach {

                    val isLocalReleaseBuild = !it.isDebuggable && !BuildEnvironment.isCIBuild
                    val releaseDebugSigningEnabled =
                        loadGradleProperty("com.spyfall.releaseDebugSigningEnabled").toBoolean()

                    if (isLocalReleaseBuild && releaseDebugSigningEnabled) {
                        // set signing config to debug so that devs can test release builds locally without signing
                        it.signingConfig = signingConfigs.getByName("debug")
                        // prefix apk with indicator that the signing is invalid
                        archivesName.set("debugsigned-${archivesName.get()}")
                    }
                }
            }

            configureGitHooksCheck()
            checkForAppModuleSecretFiles()
        }
    }
}
