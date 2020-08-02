package com.dangerfield.spyfall.ui.start

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Player
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.testUtil.getOrAwaitValue
import com.dangerfield.spyfall.util.EpochGetter
import com.dangerfield.spyfall.api.GameService
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.util.PreferencesService
import com.dangerfield.spyfall.util.SavedSessionHelper
import com.google.android.gms.tasks.Tasks
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestKoinApp::class, sdk = [28])
class StartViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()
    private val preferencesHelper = Mockito.mock(PreferencesService::class.java)
    private val gameService = Mockito.mock(GameService::class.java)
    private val epochGetter = Mockito.mock(EpochGetter::class.java)
    private val savedSessionHelper =
        SavedSessionHelper(preferencesHelper, gameService, testDispatcher, epochGetter)

    private val testRepository = Mockito.mock(GameRepository::class.java)

    private val currentUser = "Eli"
    private lateinit var testSubject: StartViewModel
    private val normalGame = Game(
        chosenLocation = "Chosen Location",
        chosenPacks = arrayListOf("one", "two"),
        started = false,
        playerList = arrayListOf(currentUser),
        playerObjectList = arrayListOf(),
        timeLimit = 5,
        locationList = arrayListOf("l1", "l2"),
        expiration = 1001
    )

    @Before
    fun setUp() {
        testSubject = StartViewModel(savedSessionHelper, testRepository)
        whenever(epochGetter.getCurrentEpoch()).thenReturn(1000)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `GIVEN no saved session, when checking for user in game, then post error to find user in session event`() =
        runBlocking {
            whenever(preferencesHelper.getSavedSession()).thenReturn(null)
            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved session but backend failure, when checking for user in game, then post error to find user in session event`() =
        runBlocking {
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    "",
                    Game.getEmptyGame()
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forException(Exception()))
            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN a saved session that isnt found on the backend, when checking for user in game, then post error to find user in session event`() =
        runBlocking {
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    "",
                    Game.getEmptyGame()
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(null))
            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved session & backend game found, when game is expired, then post error to find user in session event`() =
        runBlocking {
            val expiredGame = normalGame.copy(expiration = 999) //expiration < now means it expired BEFORE now

            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    "",
                    Game.getEmptyGame()
                )
            )

            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(expiredGame))
            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved game & backend playerlist doest contain name, when searching for user in game, then post error to find user in session event`() =
        runBlocking {

            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    "this wont be in there",
                    Game.getEmptyGame(),
                    "neither will this"
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(normalGame))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved game & backend player object contains names but player list doest contain name, when searching for user in game, then post error to find user in session event`() =
        runBlocking {

            val game = normalGame.copy(
                playerList = arrayListOf("random user"),
                playerObjectList = arrayListOf(Player("role", currentUser, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame()
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved game is started and player objects doesnt contain names, when searching for user in game, then post error to find user in session event`() =
        runBlocking {

            val anotherUser = "another user"
            val game = normalGame.copy(
                playerList = arrayListOf(currentUser, anotherUser ),
                playerObjectList = arrayListOf(Player("role", anotherUser, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame(),
                    "previous name"
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Error)
        }

    @Test
    fun `GIVEN saved game is started and has player in player objects , when searching for user in game, then post success to find user in session event`() =
        runBlocking {

            val anotherUser = "another user"
            val game = normalGame.copy(
                playerList = arrayListOf(currentUser, anotherUser ),
                playerObjectList = arrayListOf(Player("role", anotherUser, 0), Player("role", currentUser, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame(),
                    "previous name"
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Success)
        }

    @Test
    fun `GIVEN saved game is started and only has prev name in player objects , when searching for user in game, then post success to find user in session event`() =
        runBlocking {

            val anotherUser = "another user"
            val prev = "Billy Bob"
            val game = normalGame.copy(
                playerList = arrayListOf(currentUser, anotherUser ),
                playerObjectList = arrayListOf(Player("role", anotherUser, 0), Player("role", prev, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame(),
                    prev
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Success)
        }

    @Test
    fun `GIVEN saved game is started and only has prev name in both lists , when searching for user in game, then post success to find user in session event`() =
        runBlocking {

            val anotherUser = "another user"
            val prev = "Billy Bob"
            val game = normalGame.copy(
                playerList = arrayListOf(prev, anotherUser ),
                playerObjectList = arrayListOf(Player("role", anotherUser, 0), Player("role", prev, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame(),
                    prev
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Success)
        }

    @Test
    fun `GIVEN saved game and backend has current name in player list but prev name in player objects, when searching for user in game, then post success to find user in session event`() =
        runBlocking {

            val anotherUser = "another user"
            val prev = "Billy Bob"
            val game = normalGame.copy(
                playerList = arrayListOf(prev, anotherUser ),
                playerObjectList = arrayListOf(Player("role", anotherUser, 0), Player("role", currentUser, 0))
            )
            whenever(preferencesHelper.getSavedSession()).thenReturn(
                Session(
                    "",
                    currentUser,
                    Game.getEmptyGame(),
                    prev
                )
            )
            whenever(gameService.getGame(any())).thenReturn(Tasks.forResult(game))

            testSubject.triggerSearchForUserInExistingGame()
            val value = testSubject.getSearchForUserInGameEvent().getOrAwaitValue()
            assert(value?.getContentIfNotHandled() is Resource.Success)
        }

    //TODO test when user is removed from session and then another trigger to search that nothing is found (ERROR result)
}