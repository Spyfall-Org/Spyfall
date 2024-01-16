package com.dangerfield.libraries.config.internal

import com.dangerfield.libraries.config.ConfigOverride
import com.dangerfield.libraries.config.internal.model.BasicMapBasedAppConfigMapMap
import com.dangerfield.libraries.config.AppConfigMap
import timber.log.Timber

fun AppConfigMap.applyOverrides(overrides: List<ConfigOverride<Any>>): AppConfigMap {
    val mutableMap = map.toMutableMap()
    overrides.forEach {
        setValueForPath(mutableMap, it.path, it.value)
    }
    Timber.d("Applying overrides. Final map is:\n $mutableMap")
    return BasicMapBasedAppConfigMapMap(mutableMap)
}

/**
 * set a value of type [T] in the given [root] with optional appended [path].
 * This is a convenience accessor to [setValueRecursive] to allow to pass the path as a vararg instead of a list.
 */
inline fun <reified T : Any> setValueForPath(
    outputMap: MutableMap<String, Any?>,
    root: String,
    vararg path: String,
    value: T
) = setValueRecursive(outputMap, listOf(root) + path.toList(), value)

inline fun <reified T : Any> setValueForPath(
    outputMap: MutableMap<String, Any?>,
    root: String,
    path: List<String>,
    value: T
) = setValueRecursive(outputMap, listOf(root) + path.toList(), value)

inline fun <reified T : Any> setValueForPath(
    outputMap: MutableMap<String, Any?>,
    fullPath: String,
    value: T
) = setValueRecursive(outputMap, fullPath.split("."), value)

tailrec fun <T : Any> setValueRecursive(
    outMap: MutableMap<String, Any?>,
    path: List<String>,
    value: T
) {
    val key = path.first()
    if (path.size == 1) {
        outMap[key] = value
    } else {
        val child = outMap.getOrPut(key) {
            outMap[key] as? MutableMap<String, Any?> ?: mutableMapOf<String, Any?>()
        }
        setValueRecursive(outMap = child as MutableMap<String, Any?>, path.drop(1), value)
    }
}