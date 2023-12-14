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

            hasNotStarted(game)
            -> GameState.Waiting(
                accessCode = accessCode,
                players = game.players,
            )

            isBeingStarted(game)
            -> GameState.Starting(
                accessCode = accessCode,
                players = game.players
            )

            startedAt != null
                    && game.players.everyoneHasARole()
                    && remainingMillis(startedAt, game.timeLimitMins) > 0
            -> GameState.Started(
                accessCode = accessCode,
                players = game.players,
                startedAt = startedAt,
                timeLimitMins = game.timeLimitMins,
                firstPlayer = game.players.first(),
                location = game.locationName,
                locationNames = game.locationOptionNames,
                timeRemainingMillis = remainingMillis(startedAt, game.timeLimitMins)
            )

            startedAt != null
                    && game.players.everyoneHasARole()
                    && remainingMillis(startedAt, game.timeLimitMins) <= 0
                    && !game.players.everyoneHasVoted()
            -> GameState.Voting(
                accessCode = accessCode,
                players = game.players,
                location = game.locationName,
                hasMePlayerVoted = game.player(session.user.id)?.hasVoted() == true
            )

            startedAt != null
                    && game.players.everyoneHasARole()
                    && remainingMillis(startedAt, game.timeLimitMins) <= 0
                    && game.players.everyoneHasVoted()
            -> GameState.VotingEnded(
                accessCode = accessCode,
                result = calculateGameResult(game)
            )

            else -> GameState.Unknown(game = game).also {
                Timber.e("""
                    Unknown game state for game:
                    accessCode: $accessCode
                    startedAt: $startedAt
                    isBeingStarted: ${game.isBeingStarted}
                    remainingMillis: ${remainingMillis(startedAt ?: 0, game.timeLimitMins)}
                    everyoneHasARole: ${game.players.everyoneHasARole()}
                    everyoneHasVoted: ${game.players.everyoneHasVoted()}
                    remainingTimeMilis = ${startedAt?.let { remainingMillis(it, game.timeLimitMins) }}
                """.trimIndent())
            }
        }
    }

    private fun isBeingStarted(game: Game) = (game.isBeingStarted
            && (game.startedAt == null || !game.players.everyoneHasARole()))

    private fun hasNotStarted(game: Game) = (!game.isBeingStarted
            && game.startedAt == null
            && !game.players.everyoneHasARole())

    private fun calculateGameResult(game: Game): GameResult {
        val oddOneOut = game.players.find { it.isOddOneOut } ?: return GameResult.Error
        val playersWon =
            game.players.filter { it.votedCorrectly == true }.size == game.players.size - 1
        val oddOneOutWon = oddOneOut.votedCorrectly == true

        return when {
            playersWon && oddOneOutWon -> GameResult.Draw
            playersWon -> GameResult.PlayersWon
            oddOneOutWon -> GameResult.OddOneOutWon
            else -> GameResult.Draw
        }
    }

    private fun Collection<Player>.everyoneHasARole() = this.all { it.role != null }
    private fun Collection<Player>.everyoneHasVoted() = this.all { it.votedCorrectly != null }

}