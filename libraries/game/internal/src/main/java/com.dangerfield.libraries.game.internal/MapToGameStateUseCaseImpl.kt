package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.session.Session
import se.ansman.dagger.auto.AutoBind
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AutoBind
class MapToGameStateUseCaseImpl @Inject constructor(
    private val clock: Clock,
    private val gameConfig: GameConfig,
    private val session: Session
) : MapToGameStateUseCase {

    private fun remainingMillis(startedAt: Long, timeLimitMins: Int): Long {
        val currentTimeMillis = clock.millis()
        val timeLimitInMillis = if (gameConfig.forceShortGames) {
            10.seconds.inWholeMilliseconds.toInt()
        } else {
            timeLimitMins * 60 * 1000
        }

        return timeLimitInMillis - (currentTimeMillis - startedAt)
    }

    @Suppress("LongMethod")
    override fun invoke(accessCode: String, game: Game?): GameState {
        val startedAt = game?.startedAt

        return when {
            accessCode.isEmpty() || game == null -> GameState.DoesNotExist(accessCode)

            isExpired(game) -> GameState.Expired(accessCode)

            !hasStarted(game) && !game.isBeingStarted
            -> GameState.Waiting(
                accessCode = accessCode,
                players = game.players,
                videoCallLink = game.videoCallLink.takeIf { !it.isNullOrEmpty() }
            )

            !hasStarted(game) && game.isBeingStarted
            -> GameState.Starting(
                accessCode = accessCode,
                players = game.players
            )

            hasStarted(game)
                    && startedAt != null
                    && remainingMillis(startedAt, game.timeLimitMins) > 0
            -> GameState.Started(
                accessCode = accessCode,
                players = game.players,
                startedAt = startedAt,
                timeLimitMins = game.timeLimitMins,
                videoCallLink = game.videoCallLink.takeIf { !it.isNullOrEmpty() },
                firstPlayer = game.players.first(),
                location = game.locationName,
                locationNames = game.locationOptionNames,
                timeRemainingMillis = remainingMillis(startedAt, game.timeLimitMins)
            )

            hasStarted(game)
                    && startedAt != null
                    && remainingMillis(startedAt, game.timeLimitMins) <= 0
                    && !game.players.everyoneHasVoted()
            -> GameState.Voting(
                accessCode = accessCode,
                players = game.players,
                location = game.locationName,
                hasMePlayerVoted = game.player(session.user.id)?.hasVoted() == true,
                locationNames = game.locationOptionNames,
                videoCallLink = game.videoCallLink.takeIf { !it.isNullOrEmpty() }
            )

            startedAt != null
                    && hasStarted(game)
                    && remainingMillis(startedAt, game.timeLimitMins) <= 0
                    && game.players.everyoneHasVoted()
            -> GameState.VotingEnded(
                accessCode = accessCode,
                result = calculateGameResult(game),
                locationNames = game.locationOptionNames,
                players = game.players,
                location = game.locationName,
                videoCallLink = game.videoCallLink.takeIf { !it.isNullOrEmpty() }
            )

            else -> GameState.Unknown(game = game).also {
                Timber.e(
                    """
                    Unknown game state for game:
                    accessCode: $accessCode
                    startedAt: $startedAt
                    isBeingStarted: ${game.isBeingStarted}
                    remainingMillis: ${remainingMillis(startedAt ?: 0, game.timeLimitMins)}
                    everyoneHasARole: ${game.players.everyoneHasARole()}
                    everyoneHasVoted: ${game.players.everyoneHasVoted()}
                    remainingTimeMillis = ${
                        startedAt?.let {
                            remainingMillis(
                                it,
                                game.timeLimitMins
                            )
                        }
                    }
                """.trimIndent()
                )
            }
        }
    }

    private fun isExpired(game: Game): Boolean {
        val lastActive = game.lastActiveAt ?: return false
        val currentTime = clock.instant()
        val minsSinceLastActivity = currentTime.minusMillis(lastActive).epochSecond / 60
        return minsSinceLastActivity >= gameConfig.gameInactivityExpirationMins
    }

    private fun hasStarted(game: Game) = game.startedAt != null && game.players.everyoneHasARole()

    private fun calculateGameResult(game: Game): GameResult {
        val oddOneOut = game.players.find { it.isOddOneOut } ?: return GameResult.Error
        val players = (game.players - oddOneOut)

        val majorityPlayersVotedCorrectly = players.count { it.votedCorrectly() } > players.size / 2

        return when {
            majorityPlayersVotedCorrectly && oddOneOut.votedCorrectly() -> GameResult.Draw
            majorityPlayersVotedCorrectly -> GameResult.PlayersWon
            oddOneOut.votedCorrectly() -> GameResult.OddOneOutWon
            else -> GameResult.Draw
        }
    }

    private fun Collection<Player>.everyoneHasARole() = this.all { it.role != null }
    private fun Collection<Player>.everyoneHasVoted() = this.all { it.votedCorrectly != null }
}