package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.tryWithTimeout
import com.dangerfield.libraries.game.LocationPack
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.LocationPacksResult
import com.dangerfield.libraries.network.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import oddoneout.core.Catching
import oddoneout.core.logOnFailure
import oddoneout.core.success
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AutoBind
class LocationPackRepositoryImpl @Inject constructor(
    private val firebaseLocationPacksDataSource: FirebaseLocationPacksDataSource,
    private val cachedLocationPacksDataSource: CachedLocationPacksDataSource,
    private val fallbackLocationPacksDataSource: JsonFallbackLocationPacksDataSource,
    private val networkMonitor: NetworkMonitor,
    @ApplicationScope private val appScope: CoroutineScope
) : LocationPackRepository {

    override suspend fun getPacks(
        languageCode: String,
        version: Int,
        recover: Boolean
    ): Catching<LocationPacksResult> = Catching {
        val cached = cachedLocationPacksDataSource.getLocationPacks(
            version = version,
            langaugeCode = languageCode
        ).getOrNull()

        if (cached != null) {
            LocationPacksResult.Hit(cached.locationPacks)
        } else {
            val timeout = if (networkMonitor.isOnline.value) 5.seconds else 0.seconds
            tryWithTimeout(timeout) {
                firebaseLocationPacksDataSource.loadPacks(
                    language = languageCode,
                    packsVersion = version
                )
            }
                .logOnFailure()
                .onSuccess {
                    savePacks(version, languageCode, it)
                }
                .map { LocationPacksResult.Hit(it) }
                .recoverCatching {
                    if (!recover) throw it

                    val fallback = getFallbackPacks(languageCode).getOrThrow()
                    LocationPacksResult.Miss(
                        version = fallback.version,
                        packs = fallback.locationPacks
                    )
                }
                .getOrThrow()
        }
    }

    private fun savePacks(
        version: Int, languageCode: String, it: List<LocationPack>
    ) {
        // launch in app scope so that if calling scope closes, we still save
        appScope.launch {
            cachedLocationPacksDataSource.cacheLocationPacks(
                version = version,
                langaugeCode = languageCode,
                locationPacks = it
            )
        }
    }

    private suspend fun getFallbackPacks(languageCode: String): Catching<CachedLocationPack> {
        val mostRecentCached = cachedLocationPacksDataSource.getLocationPacks(
            langaugeCode = languageCode
        ).mapCatching { packs ->
            packs.maxByOrNull { it.version }
        }.getOrNull()

        val jsonFallback =
            fallbackLocationPacksDataSource.loadFallbackPack(languageCode).getOrNull()

        return when {
            jsonFallback != null && mostRecentCached != null -> {
                // take whichever has a higher version
                if (jsonFallback.version > mostRecentCached.version) {
                    jsonFallback.success()
                } else {
                    mostRecentCached.success()
                }
            }

            jsonFallback != null -> {
                jsonFallback.success()
            }
            mostRecentCached != null -> {
                mostRecentCached.success()
            }
            else -> {
                Catching.failure(Exception("No fallback or cached packs found"))
            }
        }
            .logOnFailure()
    }

    override suspend fun getPack(
        languageCode: String, version: Int, name: String
    ): Catching<LocationPack> = getPacks(
        languageCode = languageCode,
        version = version
    ).mapCatching { result ->
        when (result) {
            is LocationPacksResult.Hit -> result.packs.first { it.name == name }
            is LocationPacksResult.Miss -> throw IllegalStateException("No pack found for $languageCode, $version")
        }
    }.logOnFailure()

    override suspend fun getRoles(
        languageCode: String, version: Int, location: String
    ): Catching<List<String>> = getPacks(
        languageCode = languageCode,
        version = version
    ).mapCatching { result ->
        when (result) {
            is LocationPacksResult.Hit -> {
                val packWithLocation = result.packs.firstOrNull { pack ->
                    pack.locations.any { it.name == location }
                }
                packWithLocation?.locations?.firstOrNull { it.name == location }?.roles!!
            }
            is LocationPacksResult.Miss -> throw IllegalStateException("No pack found for $languageCode, $version")
        }
    }.logOnFailure()
}
