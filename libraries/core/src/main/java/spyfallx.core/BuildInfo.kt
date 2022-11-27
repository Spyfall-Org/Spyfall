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
) {

    val isDebug: Boolean = BuildConfig.DEBUG
}

/**
 * All applications build from the Spyfall codebase
 */
enum class TargetApp {
    SPYFALL,
    WEREWOLF,
}
