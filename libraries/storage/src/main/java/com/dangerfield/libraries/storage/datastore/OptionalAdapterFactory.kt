package com.dangerfield.libraries.storage.datastore

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Optional

class OptionalAdapterFactory<T: Any> : JsonAdapter.Factory {

    /**
     * Returns a JSON adapter for type, or null if this factory doesn't support type.
     * @param type The optional type for which to create a JSON adapter. Ex: Optional<String>
     */
    override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<Optional<T>> {
        if (annotations.isNotEmpty()) throw IllegalArgumentException("Unexpected annotations: $annotations")
        if (type !is ParameterizedType) throw IllegalArgumentException("Optional must be parameterized")
        val rawType: Class<*> = Types.getRawType(type)
        if (rawType != Optional::class.java) throw IllegalArgumentException("Unexpected type: $type")
        val actualType: Type = (type as ParameterizedType).actualTypeArguments[0]
        val optionalTypeAdapter: JsonAdapter<T> = moshi.adapter<T>(actualType).nullSafe()
        return OptionalJsonAdapter<T>(optionalTypeAdapter)
    }

    private class OptionalJsonAdapter<T: Any>(optionalTypeAdapter: JsonAdapter<T>) :
        JsonAdapter<Optional<T>>() {
        private val optionalTypeAdapter: JsonAdapter<T>

        init {
            this.optionalTypeAdapter = optionalTypeAdapter
        }

        @Throws(IOException::class)
        override fun fromJson(reader: JsonReader): Optional<T> {
            val instance: T? = optionalTypeAdapter.fromJson(reader)
            return Optional.ofNullable(instance)
        }

        override fun toJson(writer: JsonWriter, value: Optional<T>?) {
            if (value != null && value.isPresent) {
                optionalTypeAdapter.toJson(writer, value.get())
            } else {
                writer.nullValue()
            }
        }
    }
}