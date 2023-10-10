package com.dangerfield.features.colorpicker.internal

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStream
import java.io.OutputStream

class VersionedJsonSerializer<T : Any>(
    private val serializer: KSerializer<T>,
    defaultValue: () -> T,
    private val migrations: List<Migration> = emptyList(),
) : Serializer<T> {
    private val currentVersion = migrations.size + 1

    private val defaultValueFactory = defaultValue
    override val defaultValue: T get() = defaultValueFactory()

    override suspend fun readFrom(input: InputStream): T {
        val jsonString = input.bufferedReader().use { it.readText() }

        val jsonObject = Json.parseToJsonElement(jsonString) as? JsonObject
            ?: throw CorruptionException("Failed to parse JSON")

        val version = jsonObject[KEY_VERSION]?.jsonPrimitive?.int
            ?: throw CorruptionException("Version not found in JSON")

        val data = jsonObject[KEY_DATA]
            ?: throw CorruptionException("Data not found in JSON")

        val migratedData = migrateIfNeeded(data, version)

        return Json.decodeFromJsonElement(serializer, migratedData)
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        val jsonObject = JsonObject(
            mapOf(
                KEY_VERSION to JsonPrimitive(currentVersion),
                KEY_DATA to Json.encodeToJsonElement(serializer, t)
            )
        )
        output.write(Json.encodeToString(jsonObject).toByteArray())
    }

    private suspend fun migrateIfNeeded(initialData: JsonElement, existingVersion: Int): JsonElement {
        if (existingVersion > currentVersion) {
            throw CorruptionException("Cannot downgrade to version $currentVersion from version $existingVersion")
        }
        var data = initialData
        var version = existingVersion
        while (version < currentVersion) {
            val migration = migrations[version - 1]
            data = migration.migrate(data)
            ++version
        }
        return data
    }

    companion object {
        private const val KEY_VERSION = "v"
        private const val KEY_DATA = "d"
    }

    fun interface Migration {
        @Throws(CorruptionException::class)
        suspend fun migrate(data: JsonElement): JsonElement
    }
}
