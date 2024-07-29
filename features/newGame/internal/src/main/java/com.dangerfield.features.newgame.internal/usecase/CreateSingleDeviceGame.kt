package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayItems
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import com.dangerfield.oddoneoout.features.newgame.internal.R
import oddoneout.core.Catching
import oddoneout.core.showDebugSnack
import timber.log.Timber
import java.time.Clock
import java.util.LinkedList
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@Suppress("UnusedPrivateMember")
class CreateSingleDeviceGame @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val getGamePlayItems: GetGamePlayItems,
    private val clock: Clock,
    private val gameConfig: GameConfig,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame,
    private val session: Session,
    private val dictionary: Dictionary,
) {

    suspend operator fun invoke(
        packs: List<Pack<PackItem>>,
        timeLimit: Int,
        numOfPlayers: Int,
    ): Catching<String> = Catching {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = UUID.randomUUID().toString().take(gameConfig.accessCodeLength)
        val locations = getGamePlayItems(packs = packs, isSingleDevice = true).getOrThrow()
        val location = locations.random()
        val userId = session.user.id ?: UUID.randomUUID().toString()
        val shuffledRoles = location.roles?.shuffled()?.let { LinkedList(it) }
        val defaultRole = location.roles?.first()

        val host = Player(
            id = userId,
            role = null,
            userName = "${dictionary.getString(R.string.app_player_text)} 1",
            isHost = true,
            isOddOneOut = false,
            votedCorrectly = null
        )

        val players = listOf(host) +
                (2..numOfPlayers).map {
                    Player(
                        id = UUID.randomUUID().toString(),
                        role = null,
                        userName = "${dictionary.getString(R.string.app_player_text)} $it",
                        isHost = false,
                        isOddOneOut = false,
                        votedCorrectly = null
                    )
                }

        val oddOneOutIndex = players.indices.random()

        val playersWithRoles = players.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) {
                dictionary.getString(R.string.app_theOddOneOutRole_text)
            } else {
                Timber.e("Had to use a default role for a player, this should not happen")
                shuffledRoles?.poll() ?: defaultRole
            }

            player.copy(role = role, isOddOneOut = index == oddOneOutIndex)
        }

        val game = Game(
            secret = location.name,
            packIds = packs.map { it.id },
            isBeingStarted = false,
            players = playersWithRoles,
            timeLimitMins = if (gameConfig.forceShortGames) -1 else timeLimit,
            startedAt = null,
            secretOptions = locations.map { it.name },
            videoCallLink = null,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            packsVersion = gameConfig.packsVersion,
            languageCode = session.user.languageCode
        )

        gameRepository.create(game)
            .onSuccess {
                updateActiveGame(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = userId,
                        isSingleDevice = true
                    )
                )
            }
            .map { accessCode }
            .getOrThrow()
    }

    private suspend fun checkForExistingSession() {
        if (session.activeGame != null) {
            clearActiveGame()
            showDebugSnack {
                "User is already in an existing game while creating a game."
            }
        }
    }
}