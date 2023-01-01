import com.android.build.api.dsl.ApplicationExtension
import com.spyfall.convention.shared.BuildEnvironment
import com.spyfall.convention.shared.SharedConstants
import com.spyfall.convention.shared.buildConfigField
import com.spyfall.convention.shared.configureGitHooksCheck
import com.spyfall.convention.shared.configureKotlinAndroid
import com.spyfall.convention.shared.checkForAppModuleSecretFiles
import com.spyfall.convention.shared.getPackageName
import com.spyfall.convention.shared.getVersionCode
import com.spyfall.convention.shared.getVersionName
import com.spyfall.convention.shared.loadAppProperty
import com.spyfall.convention.shared.loadGradleProperty
import com.spyfall.convention.shared.printDebugSigningWarningIfNeeded
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
