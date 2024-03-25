package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import oddoneout.core.Catching
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

class GenerateAccessCode @Inject constructor(
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val gameConfig: GameConfig
) {

    suspend operator fun invoke(): Catching<String> = Catching {
        var accessCode = randomCode()
        while (gameRepository.doesGameExist(accessCode).getOrThrow()) {
            accessCode = randomCode()
        }
        accessCode
    }

    private fun randomCode() = UUID.randomUUID().toString().take(gameConfig.accessCodeLength)
}