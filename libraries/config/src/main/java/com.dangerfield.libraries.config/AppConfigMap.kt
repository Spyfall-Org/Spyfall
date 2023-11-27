package com.dangerfield.libraries.config

import spyfallx.core.common.BuildConfig

abstract class AppConfigMap {

    abstract val map: Map<String, *>

    /**
     * Method to obtain int values since numbers are parsed into doubles by default
     */
    fun intValue(value: ConfiguredValue<Int>): Int =
        value<Number>(value).toInt()

    /**
     * Method to obtain long values since numbers are parsed into doubles by default
     */
    fun longValue(value: ConfiguredValue<Long>): Long =
        value<Number>(value).toLong()

    /**
     * Method to obtain double values.
     */
    fun doubleValue(value: ConfiguredValue<Double>): Double =
        value<Number>(value).toDouble()

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
    inline fun <reified T : Any> value(value: ConfiguredValue<T>): T =
        map.getValueForPath<T>(fullPath = value.path) ?: value.default

    inline fun <reified T : Any> experiment(experiment: Experiment<T>): T =
        if (experiment.isDebugOnly && !BuildConfig.DEBUG) experiment.control
        else map.getValueForPath<T>(fullPath = experiment.path) ?: experiment.default
}
