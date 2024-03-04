package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import com.dangerfield.libraries.game.LocationPackRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import oddoneout.core.Try
import oddoneout.core.logOnFailure
import oddoneout.core.success
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

@AutoBind
class LocationPackRepositoryImpl @Inject constructor(
    private val packParser: PackParser,
    private val firebaseFirestore: FirebaseFirestore,
) : LocationPackRepository {

    private var locationPacks: List<LocationPack> = emptyList()

    override suspend fun getPacks(): Try<List<LocationPack>> = Try {
        locationPacks.ifEmpty {
            loadPacks().getOrThrow()
        }
    }
        .logOnFailure()

    override suspend fun getPack(packName: String): Try<LocationPack> = Try {
        val packs = getPacks().getOrThrow()
        packs.first { it.name == packName }
    }

    override suspend fun getLocations(packName: String): Try<List<Location>> {
        return Try.success(emptyList())
    }

    override suspend fun getRoles(locationName: String): Try<List<String>> {
        val packs = getPacks().getOrThrow()
       packs.forEach {
           it.locations.forEach { location ->
               if(location.name == locationName) {
                   return location.roles.success()
               }
           }
       }

        return Try.failure(Exception("Location not found"))
    }

    private suspend fun loadPacks() = Try {
        Timber.d("Loading packs")
        firebaseFirestore
            .collection("packs")
            .get()
            .await()
            .documents
            .mapNotNull {
                packParser.parsePack(it.id, it.data).getOrNull()
            }
    }
        .logOnFailure()
        .onSuccess {
            locationPacks = it
        }
}
