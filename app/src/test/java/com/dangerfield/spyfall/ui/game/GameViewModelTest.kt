package com.dangerfield.spyfall.ui.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.testUtil.getOrAwaitValue
import com.dangerfield.spyfall.testUtil.getVoidTask
import com.dangerfield.spyfall.ui.newGame.NewGameViewModel
import com.dangerfield.spyfall.util.ConnectivityHelper
import com.dangerfield.spyfall.util.GameService
import com.dangerfield.spyfall.util.PreferencesService
import com.dangerfield.spyfall.util.SessionListenerService
import com.google.android.gms.tasks.Tasks
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.robolectric.annotation.Config
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestKoinApp::class, sdk = [28])
class GameViewModelTest {

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

    private lateinit var testSubject: GameViewModel
    private lateinit var currentSession: Session

    @Before
    fun setUp() {
        val currentUser = "This is me"
        val game = Game.getEmptyGame().copy(
            chosenLocation = "Chosen Location",
            chosenPacks = arrayListOf("Pack 1"),
            locationList = arrayListOf("Location 1", "Location 2", "Location 3"),
            timeLimit = 5L,
            playerList = arrayListOf(currentUser, "Another user")

        )
        currentSession = Session("adkh34", currentUser, game)
        testSubject = GameViewModel(testRepository, currentSession)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    ////////REASSIGN///////

    @Test
    fun `GIVEN backend error getting roles, WHEN reassigning roles, THEN and error should be posted to the reassign event`() = runBlockingTest {
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forException(Exception()))

        testSubject.triggerReassignRoles()
        val value = testSubject.getReassignEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown)
        )
    }
    @Test
    fun `GIVEN backend give null for roles, WHEN reassigning roles, THEN and error should be posted to the reassign event`() = runBlockingTest {
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(null))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())

        testSubject.triggerReassignRoles()
        val value = testSubject.getReassignEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown)
        )
    }

    @Test
    fun `GIVEN backend give empty for roles, WHEN reassigning roles, THEN and error should be posted to the reassign event`() = runBlockingTest {
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(listOf()))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())

        testSubject.triggerReassignRoles()
        val value = testSubject.getReassignEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown)
        )
    }

    @Test
    fun `GIVEN backend gives roles but fails pushing play objects, WHEN reassigning roles, THEN and error should be posted to the reassign event`() = runBlockingTest {
            val roles = listOf("role one", "role two", "role three")
            `when`(gameService.findRolesForLocationInPacks(any(), any()))
                .thenReturn(Tasks.forResult(roles))
            `when`(gameService.setPlayerObjectsList(any(), any()))
                .thenReturn(Tasks.forException(Exception()))

            testSubject.triggerReassignRoles()
            val value = testSubject.getReassignEvent().getOrAwaitValue()
            assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown)
            )
        }

    @Test
    fun `GIVEN backend succeeds, WHEN reassigning roles, THEN update chosen location should be called`() = runBlockingTest {
        val roles = listOf("role one", "role two", "role three")
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(roles))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())

        testSubject.triggerReassignRoles()
        verify(gameService, times(1)).updateChosenLocation(any(), any())
    }

    @Test
    fun `GIVEN backend succeeds, WHEN reassigning roles, THEN update chosen location should be called with a new location`() = runBlockingTest {
        val roles = listOf("role one", "role two", "role three")
        val originalLocation = currentSession.game.chosenLocation
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(roles))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())

        testSubject.triggerReassignRoles()
        verify(gameService, times(1)).updateChosenLocation(any(), argWhere { it != originalLocation })
    }

    @Test
    fun `GIVEN backend fails to update chosen location, WHEN reassigning roles, THEN and error should be posted to the reassign event`() = runBlockingTest {
        val roles = listOf("role one", "role two", "role three")
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(roles))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())
        `when`(gameService.updateChosenLocation(any(), any()))
            .thenReturn(Tasks.forException(Exception()))

        testSubject.triggerReassignRoles()
        val value = testSubject.getReassignEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown)
        )
    }

    @Test
    fun `GIVEN backend succeeds, WHEN reassigning roles, THEN success should be posted to the reassign event`() = runBlockingTest {
        val roles = listOf("role one", "role two", "role three")
        `when`(gameService.findRolesForLocationInPacks(any(), any()))
            .thenReturn(Tasks.forResult(roles))
        `when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(getVoidTask())
        `when`(gameService.updateChosenLocation(any(), any()))
            .thenReturn(getVoidTask())

        testSubject.triggerReassignRoles()
        val value = testSubject.getReassignEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    ////////Play again///////
    @Test
    fun `GIVEN backend fails setting game, when play again triggered, THEN post error to play again event`() = runBlockingTest {
        `when`(gameService.setGame(any(), any()))
            .thenReturn(Tasks.forException(Exception()))

        testSubject.triggerPlayAgain()
        val value = testSubject.getPlayAgainEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Error)
    }

    @Test
    fun `GIVEN backend succeeds setting game, when play again triggered, THEN post success to play again event`() = runBlockingTest {
        `when`(gameService.setGame(any(), any())).thenReturn(getVoidTask())

        testSubject.triggerPlayAgain()
        val value = testSubject.getPlayAgainEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test
    fun `GIVEN normal circumstances, when play again triggered, THEN set game should be called with new location`() = runBlockingTest {
        val originalLocation = currentSession.game.chosenLocation
        `when`(gameService.setGame(any(), any())).thenReturn(getVoidTask())
        testSubject.triggerPlayAgain()
        verify(gameService, times(1)).setGame(any(), argWhere { it.chosenLocation != originalLocation })
    }

    @Test
    fun `GIVEN normal circumstances, when play again triggered, THEN set game should be called with same accessCode`() = runBlockingTest {
        val original = currentSession.accessCode
        `when`(gameService.setGame(any(), any())).thenReturn(getVoidTask())
        testSubject.triggerPlayAgain()
        verify(gameService, times(1)).setGame(argWhere { it == original }, any())
    }

    @Test
    fun `GIVEN normal circumstances, when play again triggered, THEN set game should be called with not started game`() = runBlockingTest {
        `when`(gameService.setGame(any(), any())).thenReturn(getVoidTask())
        testSubject.triggerPlayAgain()
        verify(gameService, times(1)).setGame(any(), argWhere { !it.started })
    }

    @Test
    fun `GIVEN normal circumstances, when play again triggered, THEN set game should be called with empty player objects`() = runBlockingTest {
        `when`(gameService.setGame(any(), any())).thenReturn(getVoidTask())
        testSubject.triggerPlayAgain()
        verify(gameService, times(1)).setGame(any(), argWhere { it.playerObjectList.isEmpty() })
    }

    ///////END GAME///////////

    @Test
    fun `GIVEN backend fails, when end game triggered, post error to end game event`() = runBlockingTest {
        `when`(gameService.endGame(any())).thenReturn(Tasks.forException(Exception()))
        testSubject.triggerEndGame()
        val value = testSubject.getCurrentUserEndedGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Error)
    }

    @Test
    fun `GIVEN backend succeeds, when end game triggered, post success to end game event`() = runBlockingTest {
        `when`(gameService.endGame(any())).thenReturn(getVoidTask())
        testSubject.triggerEndGame()
        val value = testSubject.getCurrentUserEndedGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test
    fun `GIVEN backend succeeds, when end game triggered, then saved session should be removed`() = runBlockingTest {
        `when`(gameService.endGame(any())).thenReturn(getVoidTask())
        testSubject.triggerEndGame()
        verify(prefHelper, times(1)).removeSavedSession()
    }

    @Test
    fun `GIVEN backend fails, when end game triggered, then saved session should be removed`() = runBlockingTest {
        `when`(gameService.endGame(any())).thenReturn(Tasks.forException(Exception()))
        testSubject.triggerEndGame()
        verify(prefHelper, times(1)).removeSavedSession()
    }

}