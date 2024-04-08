package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.game.LocationPack
import com.google.firebase.firestore.FirebaseFirestore
import oddoneout.core.Catching
import oddoneout.core.awaitCatching
import oddoneout.core.logOnFailure
import timber.log.Timber
import javax.inject.Inject

class FirebaseLocationPacksDataSource @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val packParser: PackParser,
) {

    /**
     * Grabs all location type packs from the DB, parses them and returns them
     */
    suspend fun loadPacks(
        language: String,
        packsVersion: Int
    ): Catching<List<LocationPack>> = Catching {
        Timber.d("Loading packs")
        firebaseFirestore
            .collection("versioned-packs")
            .document(packsVersion.toString())
            .collection(language)
            .whereEqualTo("type", "location")
            .get()
            .awaitCatching()
            .map {
                it.documents.map { doc ->
                    packParser.parsePacks(doc.data.orEmpty())
                        .getOrThrow()
                }.flatten()
            }
            .logOnFailure()
            .getOrThrow()
    }
}