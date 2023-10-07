package spyfallx.core

/**
 * Typed wrapper around BuildConfig which can be injected anywhere to
 * determine the current [targetApp], [versionCode], and [versionName]
 * can be injected anywhere
 */
data class BuildInfo(
    val targetApp: TargetApp,
    val versionCode: Int,
    val versionName: String,
    val configKey: String
) {
    val isDebug: Boolean = BuildConfig.DEBUG
    val isLegacySpyfall = false//targetApp is TargetApp.Spyfall && targetApp.isLegacyBuild
}

/**
 * All applications build from the Spyfall codebase
 */
sealed class TargetApp(val appName: String) {
    class Spyfall(val isLegacyBuild: Boolean) : TargetApp("Spyfall")
}
