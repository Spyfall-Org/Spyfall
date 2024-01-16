@file:OptIn(ExperimentalStdlibApi::class)

package oddoneout.core

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

inline fun <reified T : Any> Moshi.adapter(): JsonAdapter<T> = adapter(typeOf<T>().javaType)
inline fun <reified T : Any> Moshi.adapter(vararg qualifiers: Annotation): JsonAdapter<T> =
    adapter(typeOf<T>().javaType, qualifiers.toSet())

inline fun <reified T : Any> Moshi.toJson(data: T): String = adapter<T>(typeOf<T>().javaType).toJson(data)
inline fun <reified T : Any> Moshi.toJson(data: T, vararg qualifiers: Annotation): String =
    adapter<T>(typeOf<T>().javaType, qualifiers.toSet()).toJson(data)

inline fun <reified T : Any> Moshi.readJson(data: String, lenient: Boolean = true): T? =
    readJson(typeOf<T>().javaType, data, lenient)

inline fun <reified T : Any> Moshi.readJson(data: String, vararg qualifiers: Annotation, lenient: Boolean = true): T? =
    readJson(typeOf<T>().javaType, data, lenient, qualifiers.toSet())

@PublishedApi
internal fun <T : Any> Moshi.readJson(
    type: Type,
    data: String,
    lenient: Boolean,
    qualifiers: Set<Annotation> = emptySet(),
): T? {
    if (data.isBlank()) {
        return null
    }
    val jsonAdapter = adapter<T>(type, qualifiers)
        .let { if (lenient) it.lenient() else it }
    return try {
        jsonAdapter.fromJson(data)
    }  catch (ex: JsonDataException) {
        Timber.e(ex, "Unable to parse JSON: %s", data)
        null
    } catch (ex: IOException) {
        Timber.e(ex, "Could not parse the data")
        null
    }
}