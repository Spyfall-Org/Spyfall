import com.android.build.api.dsl.ApplicationExtension
import com.spyfall.convention.shared.SharedConstants
import com.spyfall.convention.shared.buildConfigField
import com.spyfall.convention.shared.configureGitHooksCheck
import com.spyfall.convention.shared.configureKotlinAndroid
import com.spyfall.convention.shared.getVersionCode
import com.spyfall.convention.shared.getVersionName
import com.spyfall.convention.shared.task.configureAppConfigCreationTask
import com.spyfall.convention.shared.task.printRed
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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
                    buildConfigField("VERSION_CODE", versionCode)
                    buildConfigField("VERSION_NAME", versionName)
                }
            }

            configureAppConfigCreationTask()
            configureGitHooksCheck()
        }
    }
}
