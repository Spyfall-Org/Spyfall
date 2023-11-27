package com.dangerfield.libraries.config.internal.model

import android.content.Context
import com.dangerfield.libraries.config.AppConfigMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import spyfallx.core.Try
import javax.inject.Inject

/**
 * Fallback config implementation of [AppConfigMap] packaged and shipped with the app
 * This is used when the app is unable to fetch the config from the server and no config is already cached
 */
class FallbackConfigMapMapBased @Inject constructor(
    @ApplicationContext context: Context,
) : AppConfigMap() {

    @Suppress("TooGenericExceptionCaught")
    override val map: Map<String, Any> by lazy {
        val inputStream = context.assets.open("default_config.json")

        Try {
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            val json = String(buffer, Charsets.UTF_8)
            Json.decodeFromString<Map<String, Any>>(json)
        }.eitherWay {
            inputStream.close()
        }.getOrNull() ?: emptyMap()
    }
}
