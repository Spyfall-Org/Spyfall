package com.dangerfield.libraries.game.internal.packs

import android.content.Context
import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
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
    fun loadFallbackPack(language: String): Catching<CachedLocationPack> {
        return Catching {
            val resId = when (language) {
                "en" -> R.raw.fallback_location_packs_en
                "es" -> R.raw.fallback_location_packs_es
                else -> R.raw.fallback_location_packs_en
            }
            val inputStream = context.resources.openRawResource(resId)
            val jsonAdapter = moshi.adapter(JsonLocationPacks::class.java)
            inputStream.source().buffer().use { source ->
                val jsonLocationPacks = jsonAdapter.fromJson(source)!!
                CachedLocationPack(
                    version = jsonLocationPacks.version,
                    langaugeCode = jsonLocationPacks.languageCode,
                    locationPacks = jsonLocationPacks.packs.map { it.toLocationPack() }
                )
            }
        }
            .logOnFailure()
    }
}

private fun JsonLocationPack.toLocationPack(): LocationPack {
    return LocationPack(
        name = this.name,
        locations = locations.map { jsonLocation ->
            Location(
                name = jsonLocation.name,
                roles = jsonLocation.roles,
                packName = this.name
            )
        }
    )
}

@JsonClass(generateAdapter = true)
internal data class JsonLocationPacks(
    val type: String,
    val version: Int,
    val languageCode: String,
    val packs: List<JsonLocationPack>
)

@JsonClass(generateAdapter = true)
internal data class JsonLocationPack(
    val name: String,
    val locations: List<JsonLocation>,
)

@JsonClass(generateAdapter = true)
internal data class JsonLocation(
    val name: String,
    val roles: List<String>
)