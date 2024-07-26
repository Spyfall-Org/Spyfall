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
    private val oldPackParser: OldPackParser,
) {

    /**
     * Grabs all location type packs from the DB, parses them and returns them
     */
    suspend fun loadPacks(
        language: String,
        packsVersion: Int
    ): Catching<List<LocationPack>> = Catching {
        Timber.d("Loading packs with path `versioned-packs/$packsVersion/location-packs/$language`")
        firebaseFirestore
            .collection("versioned-packs")
            .document(packsVersion.toString())
            .collection("location-packs")
            .document(language)
            .get()
            .awaitCatching()
            .map { doc ->
                oldPackParser.parsePacks(doc.data.orEmpty()).getOrThrow()
            }
            .mapCatching {
                check(it.isNotEmpty())
                it
            }
            .logOnFailure()
            .getOrThrow()
    }
}