package com.dangerfield.libraries.game.internal.packs

import com.dangerfield.libraries.config.EnsureAppConfigLoaded
import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.coreflowroutines.SingletonJobRunner
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.PackResult
import com.dangerfield.libraries.game.storage.DbPackOwner.App
import com.dangerfield.libraries.game.storage.PackDao
import com.dangerfield.libraries.game.storage.PackWithItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import oddoneout.core.Catching
import oddoneout.core.daysAgo
import oddoneout.core.ignore
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
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val packsRemoteDataSource: PacksRemoteDataSource
) : PackRepository {

    private val syncRun = SingletonJobRunner(applicationScope, job = { sync() })

    override suspend fun getAppPacks(
        version: Int,
        languageCode: String
    ): Catching<PackResult<Pack>> = Catching {

        syncRun.join()

        val cachedPacks = packDao.getPacksWithItems(languageCode = languageCode, owner = App)
        val packsByVersion = cachedPacks.groupBy { it.pack.version }
        val hitPacks = packsByVersion[version]

        if (hitPacks != null) {

            checkAppPacks(hitPacks, version, languageCode)

            val packs = hitPacks.map { it.toPack() }

            PackResult.Hit(packs)

        } else {
            val highestVersion =
                packsByVersion.keys.maxOrNull() ?: throw IllegalStateException("No packs found")
            val highestVersionPacks =
                packsByVersion[highestVersion] ?: throw IllegalStateException("No packs found")
            val packs = highestVersionPacks.map { it.toPack() }

            PackResult.Miss(highestVersion, packs)
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

    override suspend fun getUsersSavedPacks(): Catching<List<Pack>> {
        return Catching {
            syncRun.join()

            val cachedPacks = packDao.getUserSavedPacksWithItems()

            cachedPacks.map { it.toPack() }
        }
    }

    override suspend fun savePack(packId: String): Catching<Unit> = Catching {
        packDao.setPackSaved(packId, true)
    }

    override suspend fun updateLastAccessed(packId: String): Catching<Unit> = Catching {
        packDao.updatePackAccessed(packId, clock.millis())
    }

    override suspend fun getPack(
        version: Int,
        languageCode: String,
        id: String
    ): Catching<Pack> {
        return Catching {
            syncRun

            val cachedPack = packDao.getPacksWithItems(id = id)

            if (cachedPack == null) {
                if (id.contains(CUSTOM_PACK_PREFIX)) {
                    packsRemoteDataSource.getCommunityPack(id)
                        .onSuccess {
                            applicationScope.launch {
                                packDao.insertPacks(listOf(it.toPackEntity()))
                            }
                        }
                } else {
                    packsRemoteDataSource.getAppPacks(
                        languageCode = languageCode,
                        packsVersion = version
                    ).mapCatching {
                        applicationScope.launch {
                            packDao.insertPacks(it.map { pack -> pack.toPackEntity() })
                        }
                        it.first { pack -> pack.id == id }
                    }
                }
                    .getOrThrow()
                    .toPack()

            } else {
                cachedPack.toPack()
            }
        }
    }

    private suspend fun sync() = Catching {
        ensureAppConfigLoaded()
        val allPacks = packDao.getAllPacks().toSet()
        val accessRecords = packDao.getAccessRecords().toSet()

        val oldPackIds = accessRecords
            .filter { it.lastAccessed.daysAgo(clock.instant()) >= 30 }
            .map { it.packId }.toSet()

        val oldPacks = allPacks.filter { it.id in oldPackIds }.toSet()
        val notOldPacks = (allPacks - oldPacks).toSet()
        val hasPackNeededToStartAGame =
            notOldPacks.any { it.version == gameConfig.packsVersion && it.languageCode == getAppLanguageCode() }

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

        if (!hasPackNeededToStartAGame) {
            packsRemoteDataSource
                .getAppPacks(
                    languageCode = getAppLanguageCode(),
                    packsVersion = gameConfig.packsVersion
                )
                .onSuccess { packs ->
                    val entities = packs.map {
                        it.toPackEntity()
                    }

                    packDao.insertPacks(entities)
                }
        }
    }.ignore()

    companion object {
        private val CUSTOM_PACK_PREFIX = "custom_pack_"
    }
}