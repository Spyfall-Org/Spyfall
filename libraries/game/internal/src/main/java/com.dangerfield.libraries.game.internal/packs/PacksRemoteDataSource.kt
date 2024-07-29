package com.dangerfield.libraries.game.internal.packs

import com.google.firebase.firestore.FirebaseFirestore
import oddoneout.core.Catching
import oddoneout.core.awaitCatching
import oddoneout.core.logOnFailure
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import javax.inject.Inject

interface PacksRemoteDataSource {

    suspend fun getAppPacks(
        languageCode: String,
        packsVersion: Int
    ): Catching<List<RemotePack>>

    suspend fun getCommunityPack(id: String): Catching<RemotePack>

    suspend fun getCommunityPacks(): Catching<List<RemotePack>>
}

data class RemotePack(
    val name: String,
    val id: String,
    val groupId: String?,
    val version: Int,
    val languageCode: String,
    val ownerId: String?,
    val type: String,
    val isPublic: Boolean,
    val packItems: List<RemotePackItem>,
    val saves: Int = 0
)

data class RemotePackItem(
    val name: String,
    val roles: List<String>?,
)


object RemotePackConstants {
    val PACK_TYPE_LOCATION = "location"
    val PACK_TYPE_CELEBRITY = "celebrity"
}

@AutoBind
class PacksFirebaseDataSource @Inject constructor(
    private val firebase: FirebaseFirestore,
    private val appPackParser: AppPackParser,
    private val customPackParser: CustomPackParser
) : PacksRemoteDataSource {

    override suspend fun getAppPacks(
        languageCode: String,
        packsVersion: Int
    ): Catching<List<RemotePack>> {
        Timber.d("Loading app packs with version $packsVersion and language $languageCode")
        return firebase
            .collection("versioned-packs")
            .document(packsVersion.toString())
            .collection("languages")
            .document(languageCode)
            .get()
            .awaitCatching()
            .mapCatching { doc ->
                val packs = appPackParser.parsePacks(
                    version = packsVersion,
                    languageCode = languageCode,
                    data = doc.data.orEmpty()
                ).getOrThrow()

                packs.also { check(it.isNotEmpty()) }
            }
            .logOnFailure()
    }

    override suspend fun getCommunityPack(id: String): Catching<RemotePack> = Catching {
        val publicDoc = firebase
            .collection("public-custom-packs")
            .document(id)
            .get()
            .awaitCatching()
            .getOrThrow()

        val document = if (publicDoc.exists()) {
            publicDoc
        } else {
            firebase
                .collection("private-custom-packs")
                .document()
                .get()
                .awaitCatching()
                .getOrThrow()
        }

        customPackParser.parsePack(
            data = document.data.orEmpty(),
            isPublic = publicDoc.exists()
        ).getOrThrow()
    }

    override suspend fun getCommunityPacks(): Catching<List<RemotePack>> {

        return firebase
            .collection("public-custom-packs")
            .orderBy("saves")
            .limit(50)
            .get()
            .awaitCatching()
            .mapCatching {
                it.documents.map { document ->
                    customPackParser.parsePack(document.data.orEmpty(), true).getOrThrow()
                }
            }
    }
}