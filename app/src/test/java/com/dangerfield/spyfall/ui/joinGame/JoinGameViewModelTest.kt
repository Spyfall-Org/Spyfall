package com.dangerfield.spyfall.ui.joinGame

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.testUtil.getOrAwaitValue
import com.dangerfield.spyfall.testUtil.getVoidTask
import com.dangerfield.spyfall.ui.game.GameViewModel
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.util.ConnectivityHelper
import com.dangerfield.spyfall.util.GameService
import com.dangerfield.spyfall.util.PreferencesService
import com.dangerfield.spyfall.util.SessionListenerService
import com.google.android.gms.tasks.Tasks
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestKoinApp::class, sdk = [28])
class JoinGameViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val gameService = Mockito.mock(GameService::class.java)
    private val prefHelper = Mockito.mock(PreferencesService::class.java)
    private val sessionListener = Mockito.mock(SessionListenerService::class.java)

    private val testConnectivityHelper = object : ConnectivityHelper {
        override suspend fun isOnline(): Boolean = true
    }
    private val testRepository =
        Repository(gameService, sessionListener, prefHelper, testConnectivityHelper, testDispatcher)

    private lateinit var testSubject: JoinGameViewModel
    private val normalGame = Game(
        chosenLocation = "Chosen Location",
        chosenPacks = arrayListOf("one", "two"),
        started = false,
        playerList = arrayListOf("user"),
        playerObjectList = arrayListOf(),
        timeLimit = 5,
        locationList = arrayListOf("l1", "l2"),
        expiration = 0L
    )

    @Before
    fun setUp() {
        testSubject = JoinGameViewModel(testRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Given an empty username, when joining a game, then an error should be posted to join game event`() {
        testSubject.triggerJoinGame("abc123", "")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.FIELD_ERROR))
    }

    @Test
    fun `Given an empty access code, when joining a game, then an error should be posted to join game event`() {
        testSubject.triggerJoinGame("", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.FIELD_ERROR))
    }

    @Test
    fun `Given a username size over 25, when joining a game, then an error should be posted to join game event`() {
        testSubject.triggerJoinGame("abc123", "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.NAME_CHARACTER_LIMIT))
    }

    @Test
    fun `Given backend fails, when joining a game, then an error should be posted to join game event`() = runBlockingTest {
        whenever(gameService.getGame(any())).thenReturn(Tasks.forException(Exception()))
        testSubject.triggerJoinGame("abc123", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.UNKNOWN_ERROR))
    }

    @Test
    fun `Given backend cant find game, when joining a game, then an error should be posted to join game event`() = runBlockingTest {
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(null))
        testSubject.triggerJoinGame("abc123", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.GAME_DOES_NOT_EXIST))
    }

    @Test
    fun `Given backend game has 8 players, when joining a game, then an error should be posted to join game event`() = runBlockingTest {
        val fullGame = normalGame.copy(
            playerList = arrayListOf("1","2","3","4","5","6","7","8")
        )
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(fullGame))
        testSubject.triggerJoinGame("abc123", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.GAME_HAS_MAX_PLAYERS))
    }

    @Test
    fun `Given backend game that has started, when joining a game, then an error should be posted to join game event`() = runBlockingTest {
        val startedGame = normalGame.copy(
            started = true
        )
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(startedGame))
        testSubject.triggerJoinGame("abc123", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.GAME_HAS_STARTED))
    }

    @Test
    fun `Given backend game , when joining a game with taken name, then an error should be posted to join game event`() = runBlockingTest {
        val takenName = "This is taken"
        val game = normalGame.copy(
            playerList = arrayListOf("this", "and this", "and that", takenName)
        )
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))
        testSubject.triggerJoinGame("abc123", takenName)
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.NAME_TAKEN))
    }

    @Test
    fun `Given backend game fails to add player, when joining a game , then an error should be posted to join game event`() = runBlockingTest {
        whenever(gameService.addPlayer(any(), any())).thenReturn(Tasks.forException(Exception()))
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))
        testSubject.triggerJoinGame("abc123", "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(JoinGameError.COULD_NOT_JOIN))
    }

    @Test
    fun `Given everything goes well, when joining a game , success should post with session and current user`() = runBlockingTest {
        val currentUser = "Vlad the impaler"
        whenever(gameService.addPlayer(any(), any())).thenReturn(getVoidTask())
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))
        testSubject.triggerJoinGame("abc123", currentUser)
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled()?.data?.currentUser == currentUser)
    }

    @Test
    fun `Given everything goes well, when joining a game , success should post with new user in playerlist`() = runBlockingTest {
        val currentUser = "Vlad the impaler"
        whenever(gameService.addPlayer(any(), any())).thenReturn(getVoidTask())
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))
        testSubject.triggerJoinGame("abc123", currentUser)
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled()?.data?.game?.playerList?.contains(currentUser) ?: false)
    }

    @Test
    fun `Given everything goes well, when joining a game , success should post with session accesscode`() = runBlockingTest {
        val accessCode = "abc123"
        whenever(gameService.addPlayer(any(), any())).thenReturn(getVoidTask())
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))
        testSubject.triggerJoinGame(accessCode, "username")
        val value = testSubject.getJoinGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled()?.data?.accessCode == accessCode)
    }

    @Test
    fun `Given everything goes well, when joining a game , verify session saved`() = runBlockingTest {
        whenever(gameService.addPlayer(any(), any())).thenReturn(getVoidTask())
        whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))
        testSubject.triggerJoinGame("abc23", "username")
        verify(prefHelper, times(1)).saveSession(any())
    }
}