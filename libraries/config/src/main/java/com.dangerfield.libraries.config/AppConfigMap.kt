package com.dangerfield.libraries.config

import android.annotation.SuppressLint

/**
 *
 * # App configuration back by a simple map.
 *
 * -  It is not recommended to use the config directly but rather to create a class that wraps the config and exposes
 * the values you need especially if the config value may be used in more than one place.
 * This class should be used as your ***source of truth*** for config values.
 *
 * - The config should only be used to determine if the default defined in this source of truth is being overriden either via backend or a local config.
 *
 * - The app config can be injected into any class either as `AppConfig` or `Flow<AppConfig>` if needed
 *
 * example:
 *  ```kotlin
 *  class MyFeatureConfig(appConfig: AppConfig) {
 *    val myFeature = appConfig.value<String>(KEY) ?: DEFAULT
 *  }
 *  ```
 *
 */
abstract class AppConfigMap {

    abstract val map: Map<String, *>

    /**
     * Method to obtain int values since numbers are parsed into doubles by default
     */
    @SuppressLint("ConfigDocs")
    fun intValue(rootPath: String, vararg path: String): Int? =
        value<Number>(rootPath, *path)?.toInt()

    /**
     * Method to obtain long values since numbers are parsed into doubles by default
     */
    @SuppressLint("ConfigDocs")
    fun longValue(rootPath: String, vararg path: String): Long? =
        value<Number>(rootPath, *path)?.toLong()

    /**
     * Method to obtain double values.
     */
    @SuppressLint("ConfigDocs")
    fun doubleValue(rootPath: String, vararg path: String): Double? =
        value<Number>(rootPath, *path)?.toDouble()

    /**
     * Get the value aat [rootPath] + path.
     * For example, with a json like this:
     * {
     *   "minAppVersion": 4,
     *   "promoLabels": {
     *     enabled: ["ComingSoon"]
     *   }
     * }
     *
     * you can do `appConfigMap.value<Int>("minAppVersion")`
     * or `appConfigMap.value<List<String>>("promoLabels", "enabled")`
     *
     * ## Note:
     * only primitives and collections of primatives can be pulled from the map.
     */
    inline fun <reified T : Any> value(rootPath: String, vararg path: String): T? {
        return map.getValueForPath<T>(rootPath, *path)
    }
}
