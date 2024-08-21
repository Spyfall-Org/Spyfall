package com.dangerfield.features.newgame.internal.usecase

import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.MultiDeviceRepositoryName
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
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
import kotlin.time.Duration.Companion.minutes

class CreateGame @Inject constructor(
    private val generateAccessCode: GenerateOnlineAccessCode,
    @Named(MultiDeviceRepositoryName) private val gameRepository: GameRepository,
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
        packs: List<Pack<PackItem>>,
        packsVersion: Int,
        timeLimitMins: Int,
        videoCallLink: String?
    ): Catching<String> = when {
        packs.isEmpty() -> failure(CreateGameError.PacksEmpty())
        timeLimitMins < gameConfig.minTimeLimit -> failure(CreateGameError.TimeLimitTooShort())
        timeLimitMins > gameConfig.maxTimeLimit -> failure(CreateGameError.TimeLimitTooLong())
        userName.isBlank() -> failure(CreateGameError.NameBlank())
        !videoCallLink.isNullOrBlank() && !isRecognizedVideoCallLink(videoCallLink) -> failure(CreateGameError.VideoCallLinkInvalid())
        else -> create(
            userName = userName,
            packsVersion = packsVersion,
            packs = packs,
            timeLimitSeconds = timeLimitMins.minutes.inWholeSeconds.toInt(),
            videoCallLink = videoCallLink
        )
    }

    private suspend fun create(
        userName: String,
        packsVersion: Int,
        packs: List<Pack<PackItem>>,
        timeLimitSeconds: Int,
        videoCallLink: String?,
    ): Catching<String> = Catching {
        // TODO log metric on this so we can tell how many multi device games there are created
        checkForExistingSession()

        val accessCode = generateAccessCode.invoke().getOrThrow()
        val secretOptions = packs.map { it.packItems }.flatten().shuffled().take(gameConfig.itemsPerGame)
        val userId = session.user.id ?: generateLocalUUID.invoke()
        val currentPlayer = Player(
            id = userId,
            role = null,
            userName = userName,
            isHost = true,
            isOddOneOut = false,
            votedCorrectly = null
        )

        val secretItem = secretOptions.random()

        val game = Game(
            secretItem = secretItem,
            packs = packs,
            isBeingStarted = false,
            players = listOf(currentPlayer),
            timeLimitSeconds = if (gameConfig.forceShortGames) 10 else timeLimitSeconds,
            startedAt = null,
            secretOptions = secretOptions.map { it.name },
            videoCallLink = videoCallLink,
            version = CURRENT_GAME_MODEL_VERSION,
            accessCode = accessCode,
            lastActiveAt = clock.millis(),
            languageCode = getAppLanguageCode(),
            packsVersion = packsVersion,
            state = Game.State.Waiting,
            mePlayer = currentPlayer,
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