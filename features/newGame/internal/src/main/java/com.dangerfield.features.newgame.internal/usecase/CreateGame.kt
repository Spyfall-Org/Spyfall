package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GenerateLocalUUID
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import oddoneout.core.Try
import oddoneout.core.developerSnackIfDebug
import oddoneout.core.failure
import java.time.Clock
import javax.inject.Inject
import javax.inject.Named

class CreateGame @Inject constructor(
    private val generateAccessCode: GenerateAccessCode,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val generateLocalUUID: GenerateLocalUUID,
    private val isRecognizedVideoCallLink: IsRecognizedVideoCallLink,
    private val gameConfig: GameConfig,
    private val clock: Clock,
    private val session: Session,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame
) {

    // TODO
    /*
    add errors for
    bad link
     */

    suspend operator fun invoke(
        userName: String,
        packs: List<Pack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Try<String> = when {
        packs.isEmpty() -> CreateGameError.PacksEmpty.failure()
        timeLimit < gameConfig.minTimeLimit -> CreateGameError.TimeLimitTooShort.failure()
        timeLimit > gameConfig.maxTimeLimit -> CreateGameError.TimeLimitTooLong.failure()
        userName.isBlank() -> CreateGameError.NameBlank.failure()
        !videoCallLink.isNullOrBlank() && !isRecognizedVideoCallLink(videoCallLink) -> CreateGameError.VideoCallLinkInvalid.failure()
        else -> create(
            userName = userName,
            packs = packs,
            timeLimit = timeLimit,
            videoCallLink = videoCallLink
        )
    }

    private suspend fun create(
        userName: String,
        packs: List<Pack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Try<String> = Try {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = generateAccessCode.invoke().getOrThrow()
        val locations = getGamePlayLocations(packs).getOrThrow()
        val userId = session.user.id ?: generateLocalUUID.invoke()
        val currentPlayer = Player(
            id = userId,
            role = null,
            userName = userName,
            isHost = true,
            isOddOneOut = false,
            votedCorrectly = null
        )

        val game = Game(
            locationName = locations.random().name,
            packNames = packs.map { it.name },
            isBeingStarted = false,
            players = listOf(currentPlayer),
            timeLimitMins = timeLimit,
            startedAt = null,
            locationOptionNames = locations.map { it.name },
            videoCallLink = videoCallLink,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis()
        )

        gameRepository.create(game)
            .map { accessCode }
            .onSuccess {
                updateActiveGame(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = userId,
                        isSingleDevice = false
                    )
                )
            }
            .getOrThrow()
    }

    private suspend fun checkForExistingSession() {
        if (session.activeGame != null) {
            clearActiveGame()
            developerSnackIfDebug {
                "User is already in an existing game while creating a game."
            }
        }
    }
}