package com.dangerfield.libraries.config

import oddoneout.core.getValueForPath
import spyfallx.core.common.BuildConfig

/**
 * Base class for holding the map of all configured values. These values could be experiment treatments,
 * configured values, etc.
 */
abstract class AppConfigMap {

    abstract val map: Map<String, *>

    inline fun <reified T : Any> get(path: String): T? = map.getValueForPath<T>(fullPath = path)

    /**
     * Gets the configured value
     *
     * Example Config:
     * {
     *   "minAppVersion": 4,
     *   "promoLabels": {
     *     enabled: ["ComingSoon"]
     *   }
     * }
     *
     * Example Configured Value:
     * class MyConfiguredValue(): ConfiguredValue<List<String>> {
     *      override val path: String,
     *      override val default: T,
     * }
     *
     * With this a query can be made: `appConfigMap.value>(MyExperiment())` to get the value of the configured value
     *
     */
    inline fun <reified T : Any> value(value: ConfiguredValue<T>): T = value.debugOverride
        ?.takeIf { BuildConfig.DEBUG }
        ?: map.getValueForPath<T>(fullPath = value.path) ?: value.default

    inline fun <reified T : Any> experiment(experiment: Experiment<T>): T =
        if (experiment.isDebugOnly && !BuildConfig.DEBUG) experiment.control
        else map.getValueForPath<T>(fullPath = experiment.path) ?: experiment.default
}
