package com.dangerfield.libraries.game.internal.packs

import androidx.datastore.core.DataStore
import com.dangerfield.libraries.game.LocationPack
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.throwIfDebug
import javax.inject.Inject

class CachedLocationPacksDataSource @Inject constructor(
    private val dataStore: DataStore<List<CachedLocationPack>>
) {

    suspend fun getLocationPacks(
        version: Int,
        langaugeCode: String
    ): Catching<CachedLocationPack> = Catching {
        val data = dataStore.data.first()!!
        data.first {
            it.version == version
                    && it.langaugeCode == langaugeCode
        }
    }

    suspend fun getLocationPacks(
        langaugeCode: String
    ): Catching<List<CachedLocationPack>> = Catching {
        val data = dataStore.data.first()!!
        data.filter {
            it.langaugeCode == langaugeCode
        }
    }

    suspend fun cacheLocationPacks(
        version: Int,
        langaugeCode: String,
        locationPacks: List<LocationPack>
    ) = Catching {
        dataStore.updateData {
            locationPacks.map {
                CachedLocationPack(
                    version = version,
                    langaugeCode = langaugeCode,
                    locationPacks = locationPacks
                )
            }
        }
    }
        .logOnFailure()
        .throwIfDebug()
}

@JsonClass(generateAdapter = true)
data class CachedLocationPack(
    val version: Int,
    val langaugeCode: String,
    val locationPacks: List<LocationPack>
)