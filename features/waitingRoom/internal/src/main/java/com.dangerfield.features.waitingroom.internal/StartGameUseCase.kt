package com.dangerfield.features.waitingroom.internal

import androidx.core.os.bundleOf
import com.dangerfield.libraries.analytics.Metric
import com.dangerfield.libraries.analytics.MetricsTracker
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Player
import com.dangerfield.oddoneoout.features.waitingroom.internal.R
import oddoneout.core.Try
import oddoneout.core.eitherWay
import oddoneout.core.flatMap
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Named

class StartGameUseCase @Inject constructor(
    private val packsPackRepository: LocationPackRepository,
    private val metricsTracker: MetricsTracker,
    private val dictionary: Dictionary,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository
) {

    /**
     * Sets the flag for the game being started to true
     * assigns every player a role (including a random odd one out)
     * sets the started at time.
     */
    suspend operator fun invoke(
        accessCode: String,
        players: List<Player>,
        locationName: String,
        id: String,
    ): Try<Unit> = Try {
        gameRepository
            .setGameIsBeingStarted(accessCode, true)
            .getOrThrow()

        // TODO cleanup
        // this logic is hella repeated, maybe an AssignRolesUseCase?
        val roles = packsPackRepository.getRoles(locationName).getOrThrow()
        val shuffledRolesQueue = LinkedList(roles.shuffled())
        val defaultRole = roles.first()

        val shuffledPlayers = players.shuffled()
        val oddOneOutIndex = shuffledPlayers.indices.random()

        val shuffledPlayersWithRoles = shuffledPlayers.mapIndexed { index, player ->
            val role = if (index == oddOneOutIndex) {
                dictionary.getString(R.string.app_theOddOneOutRole_text)
            } else {
                shuffledRolesQueue.poll() ?: defaultRole
            }

            player.copy(role = role, isOddOneOut = index == oddOneOutIndex)
        }

        gameRepository
            .updatePlayers(accessCode, shuffledPlayersWithRoles)
            .flatMap {
                gameRepository.start(accessCode)
                    .onFailure {
                        trackGameFailedToStart(accessCode, id, locationName, players)
                    }
                    .onSuccess {
                        trackGameStarted(accessCode, id, locationName, players)
                    }
            }
            .eitherWay { gameRepository.setGameIsBeingStarted(accessCode, false) }
    }

    private fun trackGameFailedToStart(
        accessCode: String,
        id: String,
        locationName: String,
        players: List<Player>
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                ERROR_TO_START_GAME_EVENT_NAME,
                bundleOf(
                    ACCESS_CODE to accessCode,
                    USER_ID to id,
                    LOCATION_NAME to locationName,
                    PLAYER_COUNT to players.size.toString()
                )
            )
        )
    }

    private fun trackGameStarted(
        accessCode: String,
        id: String,
        locationName: String,
        players: List<Player>
    ) {
        metricsTracker.log(
            Metric.Event.Custom(
                GAME_STARTED_EVENT_NAME,
                bundleOf(
                    ACCESS_CODE to accessCode,
                    USER_ID to id,
                    LOCATION_NAME to locationName,
                    PLAYER_COUNT to players.size.toString()
                )
            )
        )
    }

    companion object {
        private const val ACCESS_CODE = "access_code"
        private const val USER_ID = "user_id"
        private const val LOCATION_NAME = "location_name"
        private const val PLAYER_COUNT = "player_count"
        private const val GAME_STARTED_EVENT_NAME = "game_started"
        private const val ERROR_TO_START_GAME_EVENT_NAME = "error_to_start_game"
    }
}