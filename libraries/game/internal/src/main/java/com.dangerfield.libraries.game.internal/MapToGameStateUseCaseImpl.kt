package com.dangerfield.libraries.game.internal

import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import se.ansman.dagger.auto.AutoBind
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@AutoBind
class MapToGameStateUseCaseImpl @Inject constructor(
    private val clock: Clock
) : MapToGameStateUseCase {

    private fun isTimeUp(startedAt: Long, timeLimitMins: Int): Boolean {
        val endTimeMillis = startedAt + timeLimitMins.minutes.inWholeMilliseconds
        return clock.instant().isAfter(Instant.ofEpochMilli(endTimeMillis))
    }

    override fun invoke(accessCode: String, game: Game?): GameState {
        val startedAt = game?.startedAt

        return when {
            game == null -> GameState.DoesNotExist(accessCode)
            !game.hasStarted -> GameState.Waiting(
                accessCode = accessCode,
                players = game.players,
            )

            game.hasStarted -> GameState.Starting(
                accessCode = accessCode,
                players = game.players
            )

            startedAt != null
                    && game.players.all { !it.role.isNullOrEmpty() }
                    && !isTimeUp(startedAt, game.timeLimitMins)
            -> GameState.Started(
                accessCode = accessCode,
                players = game.players,
                startedAt = startedAt,
                timeLimitMins = game.timeLimitMins,
                firstPlayer = game.players.first(),
                location = game.location
            )

            startedAt != null && isTimeUp(startedAt, game.timeLimitMins) -> GameState.TimedOut(
                accessCode = accessCode,
                players = game.players,
                firstPlayer = game.players.first(),
                location = game.location
            )

            else -> GameState.Unknown(
                accessCode = accessCode,
            )
        }
    }
}