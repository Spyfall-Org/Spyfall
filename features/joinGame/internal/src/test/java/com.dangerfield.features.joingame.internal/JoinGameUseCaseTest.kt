package com.dangerfield.features.joingame.internal

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.matchesPredicate
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameDataSourcError
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.Player
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.UpdateActiveGame
import com.dangerfield.libraries.test.isFailure
import com.dangerfield.libraries.test.isSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import oddoneout.core.GenerateLocalUUID
import oddoneout.core.Catching
import oddoneout.core.getExceptionOrNull
import org.junit.Test

class JoinGameUseCaseTest {

    private val validAccessCode = "123456"
    private val invalidAccessCode = "123"
    private val validUserName = "validUserName"
    private val accessCodeLengthValue = 6
    private val maxPlayersValue = 8
    private val maxNameLengthValue = 30
    private val minNameLengthValue = 2
    private val fakeGame = Game(
        accessCode = "556478",
        locationName = "Something",
        packNames = listOf(),
        isBeingStarted = false,
        players = listOf(
            Player(
                id = "123",
                role = "Emory",
                userName = "Some Role",
                isHost = false,
                isOddOneOut = false,
                votedCorrectly = false
            ),
            Player(
                id = "456",
                role = "Joe",
                userName = "Odd One Out",
                isHost = true,
                isOddOneOut = true,
                votedCorrectly = false
            ),
        ),
        timeLimitMins = 8,
        startedAt = null,
        locationOptionNames = listOf(),
        videoCallLink = null,
        version = CURRENT_GAME_MODEL_VERSION,
        lastActiveAt = null,
        languageCode = "en",
        packsVersion = 0
        
    )

    private val session: Session = mockk(relaxed = true)
    private val gameRepository: GameRepository = mockk {
        coEvery { join(any(), any(), any()) } returns Catching.success(Unit)
    }

    private val generateLocalUUID: GenerateLocalUUID = mockk()
    private val mapToGameStateUseCase: MapToGameStateUseCase = mockk()
    private val gameConfig: GameConfig = mockk {
        every { maxPlayers } returns maxPlayersValue
        every { maxNameLength } returns maxNameLengthValue
        every { minNameLength } returns minNameLengthValue
        every { accessCodeLength } returns accessCodeLengthValue
    }
    private val updateActiveGame: UpdateActiveGame = mockk(relaxed = true)
    private val clearActiveGame: ClearActiveGame = mockk(relaxed = true)

    private val joinGameUseCase = JoinGameUseCase(
        gameRepository = gameRepository,
        mapToGameState = mapToGameStateUseCase,
        gameConfig = gameConfig,
        session = session,
        updateActiveGame = updateActiveGame,
        clearActiveGame = clearActiveGame,
        generateLocalUUID = generateLocalUUID
    )

    @Test
    fun `GIVEN session exists, WHEN joining game, THEN current session should be cleared`() =
        runTest {
            mockJoinableGame()

            coEvery { session.activeGame } returns ActiveGame(
                accessCode = "123456",
                userId = "Emory",
                isSingleDevice = false
            )

            joinGameUseCase.invoke(validAccessCode, validUserName)

            coVerify { clearActiveGame.invoke() }
        }

    @Test
    fun `GIVEN no session exists, WHEN joining game, THEN current session should no be cleared`() =
        runTest {
            mockJoinableGame()

            coEvery { session.activeGame } returns null

            joinGameUseCase.invoke(validAccessCode, validUserName)

            coVerify(exactly = 0) { clearActiveGame.invoke() }
        }

    @Test
    fun `GIVEN valid access code and name WHEN session id is null THEN id should be generated`() =
        runTest {
            mockJoinableGame()

            val generatedId = "123"
            coEvery { session.user.id } returns null
            coEvery { generateLocalUUID.invoke() } returns generatedId

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isSuccess()

            coVerify {
                gameRepository.join(
                    accessCode = eq(validAccessCode),
                    userId = eq(generatedId),
                    userName = eq(validUserName)
                )
            }

            coVerify { generateLocalUUID.invoke() }
        }

    @Test
    fun `GIVEN valid access code and name WHEN join takes more than 5 seconds THEN failure should be returned`() =
        runTest {
            mockJoinableGame()

            coEvery { gameRepository.join(any(), any(), any()) } coAnswers {
                delay(6_000)
                Catching.success(Unit)
            }

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate { it is JoinGameUseCase.JoinGameError.UnknownError }
        }

    @Test
    fun `GIVEN valid access code and name WHEN join succeeds THEN success should be returned and session should be updated`() =
        runTest {
            mockJoinableGame()

            coEvery { gameRepository.join(any(), any(), any()) } returns Catching.success(Unit)

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isSuccess()
        }

    @Test
    fun `GIVEN valid access code and name WHEN game does not exist THEN failure should be returned`() =
        runTest {
            coEvery { gameRepository.getGame(any()) } returns Catching.failure(GameDataSourcError.GameNotFound("1234"))

            coEvery { mapToGameStateUseCase(any(), any()) } returns GameState.DoesNotExist(
                accessCode = fakeGame.accessCode
            )

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()


            assertThat(result.getExceptionOrNull())
                .isEqualTo(JoinGameUseCase.JoinGameError.GameNotFound)
        }

    @Test
    fun `GIVEN valid access code and name WHEN game is already started THEN failure should be returned`() =
        runTest {
            coEvery { gameRepository.getGame(any()) } returns Catching.success(fakeGame)

            coEvery { mapToGameStateUseCase(any(), any()) } returns GameState.Started(
                accessCode = fakeGame.accessCode,
                players = fakeGame.players,
                startedAt = fakeGame.startedAt ?: 0,
                timeLimitMins = fakeGame.timeLimitMins,
                timeRemainingMillis = 0,
                firstPlayer = fakeGame.players.first(),
                locationNames = fakeGame.locationOptionNames,
                location = fakeGame.locationName,
                videoCallLink = fakeGame.videoCallLink
            )

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .isEqualTo(JoinGameUseCase.JoinGameError.GameAlreadyStarted)
        }

    @Test
    fun `GIVEN valid access code and name WHEN game has max players THEN failure should be returned`() =
        runTest {

            every { gameConfig.maxPlayers } returns 8

            val players = listOf("1", "2", "3", "4", "5", "6", "7", "8").map {
                Player(
                    id = it,
                    role = "Role$it",
                    userName = "Name$it",
                    isHost = false,
                    isOddOneOut = it == "4",
                    votedCorrectly = false
                )
            }

            val game = Game(
                accessCode = "556478",
                locationName = "Something",
                packNames = listOf(),
                isBeingStarted = false,
                players = players,
                timeLimitMins = 8,
                startedAt = null,
                locationOptionNames = listOf(),
                videoCallLink = null,
                version = CURRENT_GAME_MODEL_VERSION,
                lastActiveAt = null,
                languageCode = "en",
                packsVersion = 0
            )

            mockJoinableGame(game)

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result)
                .isFailure()

            assertThat(result.getExceptionOrNull())
                .isEqualTo(JoinGameUseCase.JoinGameError.GameHasMaxPlayers(8))
        }

    @Test
    fun `GIVEN valid access code and name WHEN username was taken THEN failure should be returned`() =
        runTest {
            val takenName = "Bob"

            val players = listOf("1", "2").map {
                Player(
                    id = it,
                    role = "Role$it",
                    userName = "Name$it",
                    isHost = false,
                    isOddOneOut = it == "4",
                    votedCorrectly = false
                )
            } + Player(
                id = "3",
                role = "Role3",
                userName = takenName,
                isHost = false,
                isOddOneOut = false,
                votedCorrectly = false
            )

            val game = Game(
                accessCode = "556478",
                locationName = "Something",
                packNames = listOf(),
                isBeingStarted = false,
                players = players,
                timeLimitMins = 8,
                startedAt = null,
                locationOptionNames = listOf(),
                videoCallLink = null,
                version = CURRENT_GAME_MODEL_VERSION,
                lastActiveAt = null,
                languageCode = "en",
                packsVersion = 0
            )

            mockJoinableGame(game)

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = takenName
            )

            assertThat(result)
                .isFailure()

            assertThat(result.getExceptionOrNull())
                .isEqualTo(JoinGameUseCase.JoinGameError.UsernameTaken)
        }


    @Test
    fun `GIVEN valid access code and name WHEN join fails for unknown reason THEN failure should be returned`() =
        runTest {
            mockJoinableGame()

            coEvery { gameRepository.join(any(), any(), any()) } coAnswers {
                Catching.failure(Error())
            }

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate { it is JoinGameUseCase.JoinGameError.UnknownError }
        }

    @Test
    fun `GIVEN invalid access code WHEN join succeeds THEN failure should be returned`() =
        runTest {
            every { gameConfig.accessCodeLength } returns 6

            val result = joinGameUseCase.invoke(
                accessCode = invalidAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate { it is JoinGameUseCase.JoinGameError.InvalidAccessCodeLength }
        }

    @Test
    fun `GIVEN too short name WHEN join succeeds THEN failure should be returned`() =
        runTest {
            every { gameConfig.minNameLength } returns 3
            every { gameConfig.maxNameLength } returns 10

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = "0"
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate { it is JoinGameUseCase.JoinGameError.InvalidNameLength }
        }

    @Test
    fun `GIVEN too long name WHEN join succeeds THEN failure should be returned`() =
        runTest {
            every { gameConfig.minNameLength } returns 3
            every { gameConfig.maxNameLength } returns 10

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = "1234567891011"
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate { it is JoinGameUseCase.JoinGameError.InvalidNameLength }
        }


    @Test
    fun `GIVEN incompatible versions WHEN joining THEN failure should be returned`() =
        runTest {
            mockJoinableGame()

            coEvery { gameRepository.join(any(), any(), any()) } returns
                    Catching.failure(
                        GameDataSourcError.IncompatibleVersion(
                            isCurrentLower = true,
                            current = 0,
                            other = 1
                        )
                    )

            val result = joinGameUseCase.invoke(
                accessCode = validAccessCode,
                userName = validUserName
            )

            assertThat(result).isFailure()

            assertThat(result.getExceptionOrNull())
                .matchesPredicate {
                    it is JoinGameUseCase.JoinGameError.IncompatibleVersion
                }
        }

    private fun mockJoinableGame(game: Game? = null) {
        val finalGame = game ?: fakeGame
        coEvery { gameRepository.getGame(any()) } returns Catching.success(finalGame)

        coEvery { mapToGameStateUseCase(any(), any()) } returns GameState.Waiting(
            accessCode = fakeGame.accessCode,
            players = finalGame.players,
            videoCallLink = finalGame.videoCallLink
        )
    }
}