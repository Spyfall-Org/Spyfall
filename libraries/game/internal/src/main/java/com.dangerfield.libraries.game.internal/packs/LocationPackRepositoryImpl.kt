package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import com.dangerfield.libraries.game.LocationPackRepository
import oddoneout.core.Catching
import oddoneout.core.success
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

@AutoBind
class LocationPackRepositoryImpl @Inject constructor(
    private val firebaseLocationPacksDataSource: FirebaseLocationPacksDataSource,
    private val cachedLocationPacksDataSource: CachedLocationPacksDataSource
) : LocationPackRepository {

    override suspend fun getPacks(
        language: String,
        packsVersion: Int
    ): Catching<List<LocationPack>> = Catching {

        val cached = cachedLocationPacksDataSource.getLocationPacks(
            version = packsVersion,
            langaugeCode = language
        ).getOrNull()

        if (cached != null) {
            Timber.i("Location pack with version $packsVersion and language $language found in cache")
            cached.locationPacks
        } else {
            Timber.i("Location pack with version $packsVersion and language $language NOT found in cache")
            firebaseLocationPacksDataSource.loadPacks(
                language = language,
                packsVersion = packsVersion
            ).onSuccess {
                cachedLocationPacksDataSource.cacheLocationPacks(
                    version = packsVersion,
                    langaugeCode = language,
                    locationPacks = it
                )
            }.getOrThrow()
        }
    }

    override suspend fun getPack(
        language: String,
        packsVersion: Int,
        packName: String
    ): Catching<LocationPack> = Catching {
        val packs = getPacks(
            language = language,
            packsVersion = packsVersion
        ).getOrThrow()
        packs.first { it.name == packName }
    }

    override suspend fun getLocations(
        language: String,
        packsVersion: Int,
        packName: String
    ): Catching<List<Location>> = Catching {
        val packs = getPacks(
            language = language,
            packsVersion = packsVersion
        ).getOrThrow()

        packs.filter { it.name == packName }
            .flatMap { it.locations }
    }

    override suspend fun getRoles(
        language: String,
        packsVersion: Int,
        locationName: String
    ): Catching<List<String>> = Catching {

        val packs = getPacks(
            language = language,
            packsVersion = packsVersion
        ).getOrThrow()

        packs.forEach {
            it.locations.forEach { location ->
                if (location.name == locationName) {
                    return location.roles.success()
                }
            }
        }

        return Catching.failure(Exception("Location not found"))
    }
}
