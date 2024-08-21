package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.config.EnsureAppConfigLoaded
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.SingletonJobRunner
import com.dangerfield.libraries.coreflowroutines.onCollection
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GetNewCustomPackId
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.PackResult
import com.dangerfield.libraries.game.PackType
import com.dangerfield.libraries.game.storage.CreationState
import com.dangerfield.libraries.game.storage.DbPackOwner
import com.dangerfield.libraries.game.storage.DbPackOwner.*
import com.dangerfield.libraries.game.storage.DbPackType
import com.dangerfield.libraries.game.storage.PackDao
import com.dangerfield.libraries.game.storage.PackEntity
import com.dangerfield.libraries.game.storage.PackItemEntity
import com.dangerfield.libraries.game.storage.PackWithItems
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import oddoneout.core.Catching
import oddoneout.core.daysAgo
import oddoneout.core.ignore
import oddoneout.core.logOnFailure
import oddoneout.core.otherwise
import se.ansman.dagger.auto.AutoBind
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AutoBind
class PackRepositoryImpl @Inject constructor(
    private val packDao: PackDao,
    private val getAppLanguageCode: GetAppLanguageCode,
    private val gameConfig: GameConfig,
    private val ensureAppConfigLoaded: EnsureAppConfigLoaded,
    private val clock: Clock,
    private val session: Session,
    private val cleanUpOldPacks: CleanUpOldPacks,
    private val getNewCustomPackId: GetNewCustomPackId,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val packsRemoteDataSource: PacksRemoteDataSource,
    private val jsonFallbackLocationPacksDataSource: JsonFallbackLocationPacksDataSource
) : PackRepository {

    private val packItemRequestCache = mutableMapOf<String, PackItem>()

    private val syncRun = SingletonJobRunner(applicationScope, job = { sync() })

    override suspend fun getAppPacks(
        version: Int,
        languageCode: String
    ): Catching<PackResult> = Catching {

        syncRun.join()

        val cachedPacks = packDao.getPacksWithItems(languageCode = languageCode, owner = App)
        val packsByVersion = cachedPacks.groupBy { it.pack.version }
        val hitPacks = packsByVersion[version]

        when {
            hitPacks != null -> {
                checkAppPacks(hitPacks, version, languageCode)
                PackResult.Hit(hitPacks.map { it.toPack() })
            }
            packsByVersion.isNotEmpty() -> {
                val highestVersion =
                    packsByVersion.keys.maxOrNull() ?: throw IllegalStateException("No packs found")
                val highestVersionPacks =
                    packsByVersion[highestVersion] ?: throw IllegalStateException("No packs found")
                val packs = highestVersionPacks.map { it.toPack() }

                PackResult.Miss(highestVersion, packs)
            }
            else -> {
                val jsonPacks = jsonFallbackLocationPacksDataSource.loadFallbackPack(languageCode).getOrThrow()
                PackResult.Miss(
                    jsonPacks.version,
                    jsonPacks.toPacks()
                )
            }
        }
    }

    private fun checkAppPacks(
        hitPacks: List<PackWithItems>,
        version: Int,
        languageCode: String
    ) {
        val groupIds = hitPacks.map { it.pack.groupId }.toSet()
        val versions = hitPacks.map { it.pack.version }.toSet()
        val languageCodes = hitPacks.map { it.pack.languageCode }.toSet()

        check(groupIds.size == 1) {
            "App packs with version $version and language $languageCode have different group ids"
        }

        check(versions.size == 1) {
            "App packs with version $version and language $languageCode have different versions"
        }

        check(languageCodes.size == 1) {
            "App packs with version $version and language $languageCode have different language codes"
        }
    }

    override fun getUsersSavedPacksFlow(): Catching<Flow<List<Pack<PackItem>>>> {
        return Catching {

           packDao.getUserSavedPacksWithItemsFlow().map {
                it.map { packs ->
                    packs.toPack()
                }
           }.onCollection {
               syncRun.join()
           }
        }
    }

    override suspend fun savePack(packId: String): Catching<Unit> = Catching {
        packDao.setPackSaved(packId, true)
    }

    override suspend fun updateLastAccessed(packId: String): Catching<Unit> = Catching {
        packDao.updatePackAccessed(packId, clock.millis())
    }

    override suspend fun doesPackWithNameExist(name: String): Catching<Boolean> {
        return Catching {
            packDao.getPackWithName(name) != null
        }
    }

    override suspend fun doesPackItemWithNameExist(
        packId: String,
        name: String
    ): Catching<Boolean> {
        return Catching {
            packDao.getPackWithItems(packId)?.items?.any { it.name == name } ?: false
        }
    }

    override suspend fun getPackItem(
        itemName: String,
        version: Int,
        languageCode: String
    ): Catching<PackItem?> {
        return Catching {

            if (packItemRequestCache.containsKey(itemName)) {
                return@Catching packItemRequestCache[itemName]
            }

            syncRun.join()

            val cachedPacks = packDao.getPacksWithItems(
                version = version,
                languageCode = languageCode,
            )

            val packsWithMatchingItem =  cachedPacks.filter { it.items.any { item -> item.name == itemName } }

            val result = packsWithMatchingItem.map { pack ->
                pack.items.first { item -> item.name == itemName }.toPackItem(pack.pack)
            }.firstOrNull()

            result?.let { packItemRequestCache[itemName] = it }

            result
        }
    }

    override suspend fun getPack(
        version: Int,
        languageCode: String,
        id: String
    ): Catching<Pack<PackItem>> {
        return Catching {
            syncRun.join()

            val cachedPack = packDao.getPackWithItems(id = id)

            cachedPack?.toPack() ?: fetchPackFromBackend(id, languageCode, version)
        }
    }

    override fun getCachedPackFlow(id: String): Catching<Flow<Pack<PackItem>>> {
        return Catching {
            packDao.getPackWithItemsFlow(id = id)
                .filterNotNull()
                .map { it.toPack() }
        }
    }

    override suspend fun updateCachedPackDetails(
        id: String,
        name: String?,
        version: Int?,
        languageCode: String?,
        isPublic: Boolean?,
        owner: OwnerDetails?,
        isUserSaved: Boolean?,
        packType: PackType?,
        isPendingSave: Boolean?,
        hasUserPlayed: Boolean?
    ): Catching<Unit> {
        val cachedPack = packDao.getPackWithItems(id = id) ?.pack

        val updatedOwnerDetails = when(owner) {
            OwnerDetails.App -> App
            is OwnerDetails.Community -> Community
            OwnerDetails.MeUser -> User
            null -> cachedPack?.dbPackOwner ?: User
        }

        val updatedPack = PackEntity(
            id = id,
            name = name.otherwise(cachedPack?.name) ?: "",
            version = version.otherwise(cachedPack?.version) ?: 0,
            languageCode = languageCode.otherwise(cachedPack?.languageCode) ?: getAppLanguageCode(),
            isPublic = isPublic.otherwise(cachedPack?.isPublic) ?: true,
            isUserSaved = isUserSaved.otherwise(cachedPack?.isUserSaved) ?: true,
            groupId = null,
            dbPackOwner = updatedOwnerDetails,
            type = when(packType) {
                PackType.Location -> DbPackType.Location
                PackType.Celebrity -> DbPackType.Celebrity
                PackType.Custom -> DbPackType.Custom
                null -> cachedPack?.type ?: DbPackType.Custom
            },
            ownerId = cachedPack?.ownerId ?: when(updatedOwnerDetails) {
                App -> null
                User -> session.user.id
                Community -> getNewCustomPackId()
            },
            isPendingSave = isPendingSave.otherwise(cachedPack?.isPendingSave),
            hasMeUserPlayed = hasUserPlayed.otherwise(cachedPack?.hasMeUserPlayed) ?: false
        )

        return Catching {
            packDao.updatePack(updatedPack)
        }.logOnFailure()
    }

    override suspend fun deletePack(id: String) {
        Catching {
            packDao.deletePack(id)
        }.logOnFailure()
    }

    override suspend fun deletePackItem(packId: String, itemName: String) {
        Catching {
            packDao.deletePackItem(packId, itemName)
        }.logOnFailure()
    }

    override suspend fun addPackItem(packId: String, item: PackItem): Catching<Unit> {
        val cachedPack = packDao.getPack(id = packId)

        val packItem = PackItemEntity(
            name = item.name,
            roles = item.roles,
            languageCode = cachedPack?.languageCode ?: getAppLanguageCode(),
            packId = packId
        )

        return Catching {
            packDao.insertPackItems(listOf(packItem))
        }.logOnFailure()
    }

    override suspend fun updatePackItem(packId: String, item: PackItem) {
        val cachedPack = packDao.getPack(id = packId)

        val packItem = PackItemEntity(
            name = item.name,
            roles = item.roles,
            languageCode = cachedPack?.languageCode ?: getAppLanguageCode(),
            packId = packId
        )

        Catching {
            packDao.updatePackItem(packItem)
        }.logOnFailure()
    }

    private suspend fun fetchPackFromBackend(
        id: String,
        languageCode: String,
        version: Int
    ): Pack<PackItem> {
        val playedPackIds = packDao.getAllPacks().filter { it.hasMeUserPlayed }.map { it.id }.toSet()

        return if (id.contains(CUSTOM_PACK_PREFIX)) {
            packsRemoteDataSource.getCommunityPack(id)
                .onSuccess {
                    applicationScope.launch {
                        packDao.insertPacks(listOf(it.toPackEntity(it.id in playedPackIds)))
                    }
                }
        } else {
            packsRemoteDataSource.getAppPacks(
                languageCode = languageCode,
                packsVersion = version
            ).mapCatching {
                applicationScope.launch {
                    packDao.insertPacks(it.map { pack -> pack.toPackEntity(pack.id in playedPackIds) })
                }
                it.first { pack -> pack.id == id }
            }
        }
            .logOnFailure()
            .getOrThrow()
            .let { it.toPack(it.id in playedPackIds) }
    }


    private suspend fun sync() = Catching {
        applicationScope.launch { cleanUpOldPacks() }
        ensureAppConfigLoaded()

        val allPacks = packDao.getAllPacks().toSet()
        val accessRecords = packDao.getAccessRecords().toSet()

        val oldPackIds = accessRecords
            .filter { it.lastAccessed.daysAgo(clock.instant()) >= 30 }
            .map { it.packId }.toSet()

        val oldPacks = allPacks.filter { it.id in oldPackIds }.toSet()
        val notOldPacks = (allPacks - oldPacks).toSet()

        val appPacksToDelete = oldPacks
            .filter { it.dbPackOwner == App }
            .filterNot {
                // even its its old, keep it if its the one the app will use
                it.version == gameConfig.packsVersion && it.languageCode == getAppLanguageCode()
            }
            .filterNot {
                // keep it if it belongs to a group that has been used in the last 30 days
                it.groupId in notOldPacks.map { p -> p.groupId }
            }.toSet()

        val otherPacksToDelete = (oldPacks - appPacksToDelete)
            // app packs are already handled
            .filterNot { it.dbPackOwner == App }
            // anything that the user didnt save but hasnt used in 30 days is gone
            .filterNot { it.isUserSaved }

        val packsToDelete = appPacksToDelete + otherPacksToDelete

        packDao.deletePacks(packsToDelete.toList())

        // fetch regardless of if we have the latest version in cache
        // breaks are versioned, otherwise every session updates

        val playedPackIds = allPacks.filter { it.hasMeUserPlayed }.map { it.id }.toSet()
        packsRemoteDataSource
            .getAppPacks(
                languageCode = getAppLanguageCode(),
                packsVersion = gameConfig.packsVersion
            )
            .onSuccess { packs ->
                val packEntities = packs.map { it.toPackEntity(it.id in playedPackIds) }
                val itemEntities = packs.map { it.toItemEntities() }.flatten()

                packDao.insertPacks(packEntities)
                packDao.insertPackItems(itemEntities)
            }

    }.ignore()
}