package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.coreflowroutines.ApplicationScope
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.session.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import oddoneout.core.Catching
import oddoneout.core.allOrFail
import java.time.Clock
import javax.inject.Inject

class MapBackendGameToDomainGame @Inject constructor(
    private val packRepository: PackRepository,
    private val getGameState: GetGameState,
    private val session: Session,
    private val clock: Clock,
    @ApplicationScope private val applicationScope: CoroutineScope
) {

    suspend operator fun invoke(backendGame: BackendGame): Catching<Game> = Catching {
        val packs = backendGame.packIds.map {
            applicationScope.async {
                packRepository.getPack(
                    id = it,
                    version = backendGame.packsVersion,
                    languageCode = backendGame.languageCode
                )
            }
        }
            .awaitAll()
            .allOrFail()
            .getOrNull()

        if (packs == null) {
            return Catching.failure(IllegalStateException("Failed to load packs"))
        }

        val gameItem =  packs.map { it.items }.flatten().first { it.name == backendGame.secretItemName }

        val mePlayerId = if (session.activeGame?.accessCode == backendGame.accessCode) {
            session.activeGame?.userId
        } else {
            session.user.id
        }

        Game(
            accessCode = backendGame.accessCode,
            secretItem = gameItem,
            packs = packs,
            isBeingStarted = backendGame.isBeingStarted,
            players = backendGame.players.toPlayers(),
            timeLimitSeconds = backendGame.timeLimitSeconds,
            startedAt = backendGame.startedAt,
            secretOptions = backendGame.items,
            videoCallLink = backendGame.videoCallLink,
            version = backendGame.gameVersion,
            lastActiveAt = backendGame.lastActiveAt,
            languageCode = backendGame.languageCode,
            packsVersion = backendGame.packsVersion,
            state = getGameState(
                elapsedSeconds = elapsedSeconds(backendGame.startedAt),
                startedAt = backendGame.startedAt,
                timeLimitSeconds = backendGame.timeLimitSeconds,
                isBeingStarted = backendGame.isBeingStarted,
                lastActiveAt = backendGame.lastActiveAt,
                hasEveryoneVoted = backendGame.players.all { it.votedCorrectly != null }
            ),
            mePlayer = backendGame.players.firstOrNull { it.id == mePlayerId }?.toPlayer()
        )
    }

    private fun elapsedSeconds(startedAt: Long?): Int {
        val startedAtMillis = startedAt ?: return 0
        val elapsedMillis: Long = clock.millis() - startedAtMillis
        return (elapsedMillis / 1000).toInt()
    }
}