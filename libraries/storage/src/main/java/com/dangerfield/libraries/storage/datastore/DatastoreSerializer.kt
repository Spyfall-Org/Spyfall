package com.dangerfield.libraries.storage.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okio.Buffer
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

class DatastoreSerializer<T : Any>(
    private val adapter: JsonAdapter<T>,
    defaultValue: () -> T,
    private val migrations: List<Migration> = emptyList(),
) : Serializer<T> {
    private val options = JsonReader.Options.of(KEY_VERSION, KEY_DATA)
    private val currentVersion = migrations.size + 1

    private val defaultValueFactory = defaultValue
    override val defaultValue: T get() = defaultValueFactory()

    override suspend fun readFrom(input: InputStream): T =
        try {
            JsonReader.of(input.source().buffer()).use { reader ->
                reader.beginObject()
                val version = reader.peekJson().use { it.readVersion() }
                reader.seekUntilValue(1)
                val migrated = migrateIfNeeded(reader, version)
                adapter.fromJson(migrated)
                    ?: throw CorruptionException("Data was null")
            }
        } catch (e: JsonDataException) {
            throw CorruptionException("Failed to parse JSON", e)
        } catch (e: JsonEncodingException) {
            throw CorruptionException("Failed to parse JSON", e)
        }

    override suspend fun writeTo(t: T, output: OutputStream) {
        JsonWriter.of(output.sink().buffer()).use { writer ->
            writer.beginObject()
            writer.name(KEY_VERSION).value(currentVersion)
            writer.name(KEY_DATA)
            adapter.toJson(writer, t)
            writer.endObject()
        }
    }

    private suspend fun migrateIfNeeded(
        initialReader: JsonReader,
        initialVersion: Int,
    ): JsonReader {
        if (initialVersion > currentVersion) {
            throw CorruptionException("Cannot downgrade to version $currentVersion from version $initialVersion")
        }
        var reader = initialReader
        var version = initialVersion
        while (version < currentVersion) {
            val buffer = Buffer()
            val migration = migrations[version - 1]
            reader.use {
                JsonWriter.of(buffer).use { writer ->
                    migration.migrate(reader, writer)
                }
            }
            reader = JsonReader.of(buffer)
            ++version
        }
        return reader
    }

    private fun JsonReader.seekUntilValue(nameIndex: Int): JsonReader {
        while (hasNext()) {
            when (selectName(options)) {
                nameIndex -> return this
                -1 -> {
                    skipName()
                    skipValue()
                }

                else -> skipValue()
            }
        }
        throw CorruptionException("Json found is invalid")
    }

    private fun JsonReader.readVersion(): Int = seekUntilValue(0).nextInt()

    companion object {
        private const val KEY_VERSION = "version"
        private const val KEY_DATA = "data"

        fun <T : Any, R : Any> Migration(
            previousAdapter: () -> JsonAdapter<T>,
            newAdapter: () -> JsonAdapter<R>,
            migrate: (T) -> R,
        ): Migration = Migration { reader, writer ->
            val previous = previousAdapter().fromJson(reader) ?: throw CorruptionException("Adapter returned null")
            val migrated = migrate(previous)
            newAdapter().toJson(writer, migrated)
        }
    }

    fun interface Migration {

        @Throws(CorruptionException::class)
        suspend fun migrate(
            reader: JsonReader,
            writer: JsonWriter,
        )
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> VersionedDataSerializer(
    moshi: Moshi,
    noinline defaultValue: () -> T,
    migrations: List<DatastoreSerializer.Migration> = emptyList(),
): DatastoreSerializer<T> = DatastoreSerializer(moshi.adapter(typeOf<T>().javaType), defaultValue, migrations)
