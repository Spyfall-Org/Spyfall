package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Location
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.Pack
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import se.ansman.dagger.auto.AutoBind
import spyfallx.core.Try
import spyfallx.core.logOnError
import spyfallx.core.success
import timber.log.Timber
import javax.inject.Inject

@AutoBind
class LocationPackRepositoryImpl @Inject constructor(
    private val packParser: PackParser,
    private val firebaseFirestore: FirebaseFirestore
) : LocationPackRepository {

    private var packs: List<Pack> = emptyList()

    override suspend fun getPacks(): Try<List<Pack>> = Try {
        if (packs.isEmpty()) {
            loadPacks().getOrThrow()
        } else {
            packs
        }
    }
        .logOnError()

    override suspend fun getPack(packName: String): Try<Pack> {
        return Try.Failure(Exception("Not implemented"))
    }

    override suspend fun getLocations(packName: String): Try<List<Location>> {
        return Try.just(emptyList())

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

        return Try.Failure(Exception("Location not found"))
    }

    private suspend fun loadPacks() = Try {
        Timber.d("Loading packs")
        firebaseFirestore
            .collection(PACKS_COLLECTION_KEY)
            .get()
            .await()
            .documents
            .map {
                packParser
                    .parsePack(it.id, it.data!!)
                    .getOrThrow()
            }
    }
        .logOnError()
        .onSuccess {
            packs = it
        }

    companion object {
        private const val PACKS_COLLECTION_KEY = "packs"
    }
}