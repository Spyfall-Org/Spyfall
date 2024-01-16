package oddoneout.core

import spyfallx.core.common.BuildConfig

/**
 * Typed wrapper around BuildConfig which can be injected anywhere to
 * determine the current[versionCode], and [versionName] and other needed items
 */
data class BuildInfo(
    val versionCode: Int,
    val versionName: String,
    val packageName: String,
) {
    val playStorePackageName = packageName
        .split(".")
        .filter { it != "debug" }
        .joinToString(".")

    val isDebug: Boolean = BuildConfig.DEBUG
}
