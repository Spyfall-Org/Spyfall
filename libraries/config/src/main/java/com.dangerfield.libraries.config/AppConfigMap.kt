package com.dangerfield.libraries.config

import oddoneout.core.getValueForPath
import spyfallx.core.common.BuildConfig

abstract class AppConfigMap {

    abstract val map: Map<String, *>

    inline fun <reified T : Any> get(path: String): T? = map.getValueForPath<T>(fullPath = path)

    /**
     * Get the value at [rootPath] + path.
     * For example, with a json like this:
     * {
     *   "minAppVersion": 4,
     *   "promoLabels": {
     *     enabled: ["ComingSoon"]
     *   }
     * }
     *
     * and a configued value like this:
     * class MyConfiguedValue(): ConfiguredValue<List<String>> {
     *      override val path: String,
     *      override val default: T,
     * }
     *
     * you can do `appConfigMap.value>(MyExperiment())` to get the value of the configured value
     *
     */
    inline fun <reified T : Any> value(value: ConfiguredValue<T>): T = value.debugOverride
        ?.takeIf { BuildConfig.DEBUG }
        ?: map.getValueForPath<T>(fullPath = value.path) ?: value.default

    inline fun <reified T : Any> experiment(experiment: Experiment<T>): T =
        if (experiment.isDebugOnly && !BuildConfig.DEBUG) experiment.control
        else map.getValueForPath<T>(fullPath = experiment.path) ?: experiment.default
}
