package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPack
import com.dangerfield.libraries.game.LocationPackRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import oddoneout.core.Catching
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

    override suspend fun getPacks(): Catching<List<LocationPack>> = Catching {
        locationPacks.ifEmpty {
            loadPacks().getOrThrow()
        }
    }
        .logOnFailure()

    override suspend fun getPack(packName: String): Catching<LocationPack> = Catching {
        val packs = getPacks().getOrThrow()
        packs.first { it.name == packName }
    }

    override suspend fun getLocations(packName: String): Catching<List<Location>> {
        return Catching.success(emptyList())
    }

    override suspend fun getRoles(locationName: String): Catching<List<String>> {
        val packs = getPacks().getOrThrow()
       packs.forEach {
           it.locations.forEach { location ->
               if(location.name == locationName) {
                   return location.roles.success()
               }
           }
       }

        return Catching.failure(Exception("Location not found"))
    }

    private suspend fun loadPacks() = Catching {
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
