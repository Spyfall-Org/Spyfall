package com.dangerfield.features.newgame.internal

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.matchesPredicate
import com.dangerfield.features.newgame.internal.presentation.model.CreateGameError
import com.dangerfield.features.newgame.internal.usecase.CreateGame
import com.dangerfield.features.newgame.internal.usecase.GenerateOnlineAccessCode
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.CURRENT_GAME_MODEL_VERSION
import com.dangerfield.libraries.game.Game
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GetGamePlayItems
import com.dangerfield.libraries.session.ActiveGame
import com.dangerfield.libraries.session.ClearActiveGame
import com.dangerfield.libraries.session.ColorConfig
import com.dangerfield.libraries.session.Session
import com.dangerfield.libraries.session.Stats
import com.dangerfield.libraries.session.ThemeConfig
import com.dangerfield.libraries.session.UpdateActiveGame
import com.dangerfield.libraries.session.User
import com.dangerfield.libraries.test.isFailure
import com.dangerfield.libraries.test.isSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import oddoneout.core.GenerateLocalUUID
import oddoneout.core.Catching
import oddoneout.core.getExceptionOrNull
import org.junit.Before
import org.junit.Test
import java.time.Clock

class CreateGameTest {

    private val generatedId = "unmistakablygeneratedid"
    private val generatedAccessCode = "unmistakablygeneratedAccessCode"
    private val userId = "someUserId"

    private val generateAccessCode = mockk<GenerateOnlineAccessCode>()
    private val gameRepository = mockk<GameRepository>()
    private val getGamePlayItems = mockk<GetGamePlayItems>()
    private val generateLocalUUID = mockk<GenerateLocalUUID>()
    private val gameConfig = mockk<GameConfig>()
    private val clock = mockk<Clock>()
    private val session = mockk<Session>()
    private val updateActiveGame = mockk<UpdateActiveGame>()
    private val clearActiveGame = mockk<ClearActiveGame>()
    private val isRecognizedVideoCallLink = mockk<IsRecognizedVideoCallLink>()
    private val getAppLanguageCode = mockk<GetAppLanguageCode>()

    @Before
    fun setup() {
        // sets up happy path
        coEvery { generateAccessCode.invoke() } returns Catching.success(generatedAccessCode)

        every { isRecognizedVideoCallLink.invoke(any()) } returns true

        every { getAppLanguageCode.invoke() } returns "en"

        coEvery { getGamePlayItems.invoke(any()) } answers { call ->
            @Suppress("UNCHECKED_CAST")
            val locationPacks = (call.invocation.args[0] as List<LocationPack>)
            Catching.success(locationPacks.flatMap { it.locations }.shuffled().takeLast(5))
        }

        every { session.activeGame } returns null
        every { session.user } returns User(
            id = userId,
            languageCode = "en",
            themeConfig = ThemeConfig(
                colorConfig = ColorConfig.Random,
            ),
            stats = Stats(
                multiDeviceGamesPlayed = 34,
                winsAsOddOne = listOf(),
                winsAsPlayer = listOf(),
                lossesAsOddOne = listOf(),
                lossesAsPlayer = listOf(),
                singleDeviceGamesPlayed = 34
            )
        )

        every { gameConfig.maxTimeLimit } returns 15
        every { gameConfig.minTimeLimit } returns 1
        every { gameConfig.forceShortGames } returns false

        coEvery { generateLocalUUID.invoke() } returns generatedId
        coEvery { updateActiveGame.invoke(any()) } returns Catching.success(Unit)
        coEvery { clearActiveGame.invoke() } returns Catching.success(Unit)
        coEvery { gameRepository.create(any()) } returns Catching.success(Unit)
        every { clock.millis() } returns 123456789L
    }

    private val createGame = CreateGame(
        generateAccessCode = generateAccessCode,
        gameRepository = gameRepository,
        getGamePlayItems = getGamePlayItems,
        generateLocalUUID = generateLocalUUID,
        gameConfig = gameConfig,
        clock = clock,
        session = session,
        updateActiveGame = updateActiveGame,
        clearActiveGame = clearActiveGame,
        isRecognizedVideoCallLink = isRecognizedVideoCallLink,
        getAppLanguageCode = getAppLanguageCode,
    )

    @Test
    fun `GIVEN happy path WHEN creating THEN success should return`() = runTest {

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0,
        )

        assertThat(result).isSuccess()

        assertThat(result.getOrNull()).isEqualTo(generatedAccessCode)

        coVerify {
            gameRepository.create(any())
            updateActiveGame.invoke(
                ActiveGame(
                    accessCode = generatedAccessCode,
                    userId = userId,
                    isSingleDevice = false
                )
            )
        }
    }

    @Test
    fun `GIVEN pack is empty, WHEN creating, THEN error`() = runTest {

        val result = createGame.invoke(
            userName = "name",
            packs = emptyList(),
            timeLimit = 1,
            videoCallLink = "link",
            packsVersion = 0
            )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull())
            .matchesPredicate {
                it is CreateGameError.PacksEmpty
            }
    }

    @Test
    fun `GIVEN video link invalid WHEN creating THEN failure should return`() = runTest {

        every { isRecognizedVideoCallLink.invoke(any()) } returns false

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull())
            .matchesPredicate {
                it is CreateGameError.VideoCallLinkInvalid
            }
    }

    @Test
    fun `GIVEN timelimit is too short, WHEN creating, THEN error`() = runTest {

        every { gameConfig.minTimeLimit } returns 5

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "username",
            packs = packs,
            timeLimit = 1,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull())
            .matchesPredicate {
                it is CreateGameError.TimeLimitTooShort
            }
    }

    @Test
    fun `GIVEN timelimit is too long, WHEN creating, THEN error`() = runTest {

        every { gameConfig.maxTimeLimit } returns 5

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "username",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull())
            .matchesPredicate {
                it is CreateGameError.TimeLimitTooLong
            }
    }

    @Test
    fun `GIVEN name is blank, WHEN creating, THEN error`() = runTest {

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull())
            .matchesPredicate {
                it is CreateGameError.NameBlank
            }
    }

    @Test
    fun `GIVEN existing session WHEN creating THEN it should be cleared`() = runTest {

        every { session.activeGame } returns ActiveGame(
            accessCode = "123456",
            userId = "234234",
            isSingleDevice = false
        )

        val packs = getFilledOutPacks()

        createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        coVerify { clearActiveGame.invoke() }

    }

    @Test
    fun `GIVEN no existing session WHEN creating THEN it should not be cleared`() = runTest {

        every { session.activeGame } returns null

        val packs = getFilledOutPacks()

        createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        coVerify(exactly = 0) { clearActiveGame.invoke() }
    }


    @Test
    fun `GIVEN error generating access code WHEN creating THEN error`() = runTest {
        val someError = Error("some")
        coEvery { generateAccessCode.invoke() } returns Catching.failure(someError)

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull()).isEqualTo(someError)
    }

    @Test
    fun `GIVEN error getting locations WHEN creating THEN error`() = runTest {
        val someError = Error("cant get locations for some reason")

        coEvery { getGamePlayItems(any()) } returns Catching.failure(someError)

        val packs = getFilledOutPacks()

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull()).isEqualTo(someError)
    }

    @Test
    fun `GIVEN session user id is null WHEN creating THEN generated one should be used`() =
        runTest {
            val accessCode = "444333"

            every { session.user.id } returns null
            coEvery { generateAccessCode.invoke() } returns Catching.success(accessCode)
            every { session.user.languageCode } returns "en"

            val packs = getFilledOutPacks()

            createGame.invoke(
                userName = "name",
                packs = packs,
                timeLimit = 6,
                videoCallLink = "link",
                packsVersion = 0
            )

            coVerify {
                generateLocalUUID.invoke()

                updateActiveGame.invoke(
                    ActiveGame(
                        accessCode = accessCode,
                        userId = generatedId,
                        isSingleDevice = false
                    )
                )
            }
        }

    @Test
    fun `GIVEN can WHEN creating THEN correct game values should be used`() = runTest {
        val packs = getFilledOutPacks()
        val locationsForGameplay = packs.flatMap { it.locations }.shuffled().takeLast(5)
        val currentUserName = "name"
        val currentUserId = "1234"
        val timeLimit = 6
        val videoCallLink = "link"
        val lastActive = 123456789L

        every { clock.millis() } returns lastActive
        every { session.user.id } returns currentUserId
        every { session.user.languageCode } returns "en"
        coEvery { getGamePlayItems.invoke(any()) } returns Catching.success(locationsForGameplay)

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = timeLimit,
            videoCallLink = videoCallLink,
            packsVersion = 0
        )

        assertThat(result).isSuccess()

        coVerify {
            gameRepository.create(withArg { game ->
                assertThat(game.isBeingStarted).isFalse()
                assertThat(game.startedAt).isEqualTo(null)
                assertThat(game.timeLimitMins).isEqualTo(timeLimit)
                assertThat(game.videoCallLink).isEqualTo(videoCallLink)
                assertThat(game.version).isEqualTo(CURRENT_GAME_MODEL_VERSION)
                assertThat(game.accessCode).isEqualTo(generatedAccessCode)
                assertThat(game.lastActiveAt).isEqualTo(lastActive)

                assertThat(
                    game.hasValidPlayers(
                        currentUserName = currentUserName,
                        currentUserId = currentUserId
                    )
                ).isTrue()

                assertThat(
                    game.hasValidLocations(allPackLocations = packs.flatMap { it.locations })
                ).isTrue()

                assertThat(
                    game.hasValidLocation(gameLocations = locationsForGameplay.map { it.name })
                ).isTrue()

                assertThat(
                    game.hasValidPacks(locationPacks = packs)
                ).isTrue()

            })
        }
    }

    @Test
    fun `GIVEN repo failure WHEN creating THEN filaure should return`() = runTest {
        val someError = Error("some error")
        val packs = getFilledOutPacks()

        coEvery { gameRepository.create(any()) } returns Catching.failure(someError)

        val result = createGame.invoke(
            userName = "name",
            packs = packs,
            timeLimit = 6,
            videoCallLink = "link",
            packsVersion = 0
        )

        assertThat(result).isFailure()

        assertThat(result.getExceptionOrNull()).isEqualTo(someError)

        coVerify(exactly = 0) { updateActiveGame.invoke(any()) }
    }


    private fun Game.hasValidPlayers(currentUserName: String, currentUserId: String): Boolean {
        return players.find {
            it.userName == currentUserName
                    && it.isHost
                    && it.role == null
                    && it.id == currentUserId
                    && !it.isOddOneOut
                    && it.votedCorrectly == null
        } != null
    }

    private fun Game.hasValidLocations(allPackLocations: List<OldLocationModel>) =
        allPackLocations.map { it.name }.toSet().containsAll(secretOptions.toSet())

    private fun Game.hasValidLocation(gameLocations: List<String>) =
        gameLocations.contains(secret)

    private fun Game.hasValidPacks(locationPacks: List<LocationPack>) =
        this.packIds.toSet().containsAll(locationPacks.map { it.name }.toSet())


    private fun getFilledOutLocations(packName: String): List<OldLocationModel> {
        val locations = listOf("1", "2", "3", "4", "5", "6", "7", "8").map {
            OldLocationModel(
                name = "location$it",
                roles = listOf("some role", "some other role", "odd one out"),
                packName = packName
            )
        }
        return locations
    }

    private fun getFilledOutPacks(): List<LocationPack> {
        val locationPacks = listOf("1", "2", "3").map { packNumber ->
            val packName = "packName$packNumber"
            LocationPack(
                name = packName,
                locations = getFilledOutLocations(packName),
            )
        }
        return locationPacks
    }
}