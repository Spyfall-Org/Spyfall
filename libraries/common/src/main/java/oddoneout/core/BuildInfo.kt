package oddoneout.core

import android.os.Build
import spyfallx.core.common.BuildConfig


/**
 * Typed wrapper around BuildConfig which can be injected anywhere to
 * determine the current[versionCode], and [versionName] and other needed items
 */
data class BuildInfo(
    val versionCode: Int,
    val versionName: String,
    val packageName: String,
    val buildType: BuildType,
    val deviceName: String = getDeviceName()
) {
    val playStorePackageName = packageName
        .split(".")
        .filter { it != "debug" }
        .joinToString(".")

    val isDebug: Boolean = BuildConfig.DEBUG
}

enum class BuildType {
    DEBUG,
    RELEASE,
    QA
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val modelContainsManufacturer = model.contains(manufacturer, ignoreCase = true)
    return if (modelContainsManufacturer) {
        model
    } else {
        "$manufacturer $model"
    }
}
