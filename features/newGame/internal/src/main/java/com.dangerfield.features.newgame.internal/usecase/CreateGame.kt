package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayLocations
import com.dangerfield.libraries.game.LocationPack
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import oddoneout.core.GenerateLocalUUID
import oddoneout.core.Catching
import oddoneout.core.showDebugSnack
import oddoneout.core.failure
import java.time.Clock
import javax.inject.Inject
import javax.inject.Named

class CreateGame @Inject constructor(
    private val generateAccessCode: GenerateOnlineAccessCode,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
    private val getGamePlayLocations: GetGamePlayLocations,
    private val generateLocalUUID: GenerateLocalUUID,
    private val isRecognizedVideoCallLink: IsRecognizedVideoCallLink,
    private val gameConfig: GameConfig,
    private val clock: Clock,
    private val session: Session,
    private val getAppLanguageCode: GetAppLanguageCode,
    private val updateActiveGame: UpdateActiveGame,
    private val clearActiveGame: ClearActiveGame,
) {

    suspend operator fun invoke(
        userName: String,
        locationPacks: List<LocationPack>,
        packsVersion: Int,
        timeLimit: Int,
        videoCallLink: String?
    ): Catching<String> = when {
        locationPacks.isEmpty() -> failure(CreateGameError.PacksEmpty)
        timeLimit < gameConfig.minTimeLimit -> failure(CreateGameError.TimeLimitTooShort)
        timeLimit > gameConfig.maxTimeLimit -> failure(CreateGameError.TimeLimitTooLong)
        userName.isBlank() -> failure(CreateGameError.NameBlank)
        !videoCallLink.isNullOrBlank() && !isRecognizedVideoCallLink(videoCallLink) -> failure(CreateGameError.VideoCallLinkInvalid)
        else -> create(
            userName = userName,
            packsVersion = packsVersion,
            locationPacks = locationPacks,
            timeLimit = timeLimit,
            videoCallLink = videoCallLink
        )
    }

    private suspend fun create(
        userName: String,
        packsVersion: Int,
        locationPacks: List<LocationPack>,
        timeLimit: Int,
        videoCallLink: String?
    ): Catching<String> = Catching {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = generateAccessCode.invoke().getOrThrow()
        val locations = getGamePlayLocations(locationPacks).getOrThrow()
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
            packNames = locationPacks.map { it.name },
            isBeingStarted = false,
            players = listOf(currentPlayer),
            timeLimitMins = if (gameConfig.forceShortGames) -1 else timeLimit,
            startedAt = null,
            locationOptionNames = locations.map { it.name },
            videoCallLink = videoCallLink,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            languageCode = getAppLanguageCode(),
            packsVersion = packsVersion
        )

        gameRepository.create(game)
            .onSuccess {
                updateActiveGame(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = userId,
                        isSingleDevice = false
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