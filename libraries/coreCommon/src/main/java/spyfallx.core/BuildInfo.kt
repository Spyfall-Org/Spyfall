package spyfallx.core

/**
 * Typed wrapper around BuildConfig which can be injected anywhere to
 * determine the current[versionCode], and [versionName] and other needed items
 */
data class BuildInfo(
    val versionCode: Int,
    val versionName: String,
    val configKey: String
) {
    val isDebug: Boolean = BuildConfig.DEBUG
    val isLegacyBuild = false
}
