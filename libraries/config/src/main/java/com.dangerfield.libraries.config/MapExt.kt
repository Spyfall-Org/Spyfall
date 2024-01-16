package com.dangerfield.libraries.config

import oddoneout.core.Try
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug

/**
 * Find a value of type [T] in the given [root] with optional appended [path].
 * This is a convenience accessor to [getValueRecursive] to allow to pass the path as a vararg instead of a list.
 */
inline fun <reified T : Any> Map<String, *>.getValueForPath(root: String, vararg path: String) =
    getValueRecursive<T>(listOf(root) + path.toList(), T::class.java)

inline fun <reified T : Any> Map<String, *>.getValueForPath(root: String, path: List<String>) =
    getValueRecursive<T>(listOf(root) + path.toList(), T::class.java)

inline fun <reified T : Any> Map<String, *>.getValueForPath(fullPath: String) =
    getValueRecursive<T>(fullPath.split("."), T::class.java)

/**
 * Recursive implementation for [getValueForPath].
 */
@Suppress("UNCHECKED_CAST", "ReturnCount")
fun <T : Any> Map<String, *>.getValueRecursive(path: List<String>, clazz: Class<*> ): T? {
    if (path.size == 1) {
        val rawValue = this[path.first()] ?: return null
        return Try {
            when (clazz) {
                String::class.java -> rawValue.toString() as? T
                Boolean::class.java -> rawValue.toString().toBoolean() as? T
                // all numbers come back as doubles. First cast to double, then to requested type
                Int::class.java -> rawValue.toString().toDoubleOrNull()?.toInt() as? T
                Number::class.java -> rawValue.toString().toDoubleOrNull()?.toInt() as? T
                Integer::class.java -> rawValue.toString().toDoubleOrNull()?.toInt() as? T
                Double::class.java -> rawValue.toString().toDoubleOrNull() as? T
                Float::class.java -> rawValue.toString().toDoubleOrNull()?.toFloat() as? T
                Byte::class.java -> rawValue.toString().toByteOrNull() as? T
                Short::class.java -> rawValue.toString().toShortOrNull() as? T
                Long::class.java -> rawValue.toString().toDoubleOrNull()?.toLong() as? T
                else -> rawValue as? T
            }
        }
            .logOnError()
            .throwIfDebug()
            .getOrNull()
        //TODO test if the throw if debug works
    }
    val subMap = this[path.first()] as? Map<String, *> ?: return null
    return subMap.getValueRecursive(path.drop(1), clazz)
}

/**
 * Creates a new map and adds the key-value pair to it if the provided [condition] is met and the [value] is not null.
 */
fun <K, V> Map<K, V>.plusIf(condition: Boolean, key: K, value: V?): Map<K, V> {
    if (condition) {
        return plusIfNotNull(key to value)
    }
    return this
}

/**
 * Creates a new map and adds the key-value [pair] to it if the value is not null.
 */
fun <T, U> Map<T, U>.plusIfNotNull(pair: Pair<T, U?>): Map<T, U> {
    return pair.second?.let {
        this.plus(pair.first to it)
    } ?: this
}

/**
 * Creates a new map and adds the key-value pair to it if the [value] is not null.
 */
fun <T, U> Map<T, U>.plusIfNotNull(key: T, value: U?): Map<T, U> {
    return plusIfNotNull(Pair(key, value))
}

/**
 * Returns a Map that now only contains the key / value pairs of non null values
 */
@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterNotNullValues() = filterValues { it != null } as Map<K, V>

/**
 * Merges current Map with given [map]. Nested Maps will be merged as well. Returns null when both maps are null.
 */
@Suppress("UNCHECKED_CAST")
fun <K> Map<K, *>?.mergeWith(map: Map<K, *>?): Map<K, *>? {
    if (this == null) return map
    val mutableBaseMap = toMutableMap()
    map?.keys?.forEach { key ->
        val baseValue = this[key]
        val newValue = map[key]
        mutableBaseMap[key] = when {
            newValue is Map<*, *> && baseValue is Map<*, *> ->
                (baseValue as Map<K, *>).mergeWith(newValue as Map<K, *>)
            else -> newValue
        }
    }
    return mutableBaseMap
}

/**
 * Merges a List of [Map]s into a single map. See [mergeWith] method.
 */
fun <K> List<Map<K, *>>.toMergedMap(): Map<K, *> {
    var map: Map<K, Any> = emptyMap()
    this.forEach {
        map = map.mergeWith(it) as Map<K, Any>
    }
    return map
}
