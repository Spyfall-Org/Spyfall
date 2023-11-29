package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import spyfallx.core.Try
import java.util.UUID
import javax.inject.Inject

class GenerateAccessCode @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameConfig: GameConfig
) {

    suspend fun invoke(): Try<String> = Try {
        var accessCode = randomCode()
        while (gameRepository.doesGameExist(accessCode).getOrThrow()) {
            accessCode = randomCode()
        }
        accessCode
    }


    private fun randomCode() = UUID.randomUUID().toString().take(gameConfig.accessCodeLength)
}