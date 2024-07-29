package com.dangerfield.libraries.game.internal.packs

import android.content.Context
import com.dangerfield.oddoneoout.libraries.game.internal.R
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import okio.buffer
import okio.source
import javax.inject.Inject

/**
 * A data source that loads location packs from a JSON file packaged with the application.
 * This data should be very rarely used as we attempt to load from cache or network first.
 */
class JsonFallbackLocationPacksDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {

    @Suppress("UnusedPrivateMember")
    fun loadFallbackPack(languageCode: String): Catching<JsonPacks> {
        return Catching {
            val resId = when (languageCode) {
                "en" -> R.raw.fallback_location_packs_en
                "es" -> R.raw.fallback_location_packs_es
                else -> R.raw.fallback_location_packs_en
            }
            val inputStream = context.resources.openRawResource(resId)
            val jsonAdapter = moshi.adapter(JsonPacks::class.java)
            inputStream.source().buffer().use { source ->
                val jsonLocationPacks = jsonAdapter.fromJson(source)!!
                jsonLocationPacks
            }
        }
            .logOnFailure()
    }

    companion object {
        const val PACK_TYPE_LOCATION = "location"
        const val PACK_TYPE_CELEBRITY = "celebrity"
    }
}

@JsonClass(generateAdapter = true)
data class JsonPacks(
    val type: String,
    val version: Int,
    val languageCode: String,
    val packs: List<JsonPack>
)

@JsonClass(generateAdapter = true)
data class JsonPack(
    val name: String,
    val locations: List<JsonLocation>,
)

@JsonClass(generateAdapter = true)
data class JsonLocation(
    val name: String,
    val roles: List<String>
)