package com.dangerfield.spyfall.ui.newGame

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.GameService
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.testUtil.getOrAwaitValue
import com.dangerfield.spyfall.testUtil.getVoidTask
import com.dangerfield.spyfall.util.*
import com.google.android.gms.tasks.Tasks
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
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
class NewGameViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val gameService: GameService = Mockito.mock(
        GameService::class.java)
    private val prefHelper: PreferencesService = Mockito.mock(PreferencesService::class.java)
    private val sessionListener: SessionListenerService =
        Mockito.mock(SessionListenerService::class.java)
    private val testConnectivityHelper = object : ConnectivityHelper {
        override suspend fun isOnline(): Boolean = true
    }
    private val testDispatcher = TestCoroutineDispatcher()
    private val testRepository = Repository(gameService, sessionListener, prefHelper, testConnectivityHelper, testDispatcher)

    private lateinit var testSubject: NewGameViewModel

    @Before
    fun setUp() {
        testSubject = NewGameViewModel(testRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    ///////CREATE GAME////////////
    @Test
    fun `given user has not selected pack, when create game is triggered, then NO_SELECTED_PACK should be posted to create game event`() {
        val selected = arrayListOf<String>()
        testSubject.triggerCreateGameEvent("username", "9", selected )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.NO_SELECTED_PACK))
    }

    @Test
    fun `given user has an empty username, when create game is triggered, then EMPTY_NAME should be posted to create game event`() {
        val username = ""
        testSubject.triggerCreateGameEvent(username, "9", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.EMPTY_NAME))
    }

    @Test
    fun `given username size greater than 25, when create game is triggered, then NAME_CHARACTER_LIMIT should be posted to create game event`() {
        val username = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        testSubject.triggerCreateGameEvent(username, "9", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.NAME_CHARACTER_LIMIT))
    }

    @Test
    fun `given time limit empty, when create game is triggered, then TIME_LIMIT_ERROR should be posted to create game event`() {
        val timeLimit = ""
        testSubject.triggerCreateGameEvent("username", timeLimit, arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.TIME_LIMIT_ERROR))
    }

    @Test
    fun `given time limit greater than 10, when create game is triggered, then TIME_LIMIT_ERROR should be posted to create game event`() {
        val timeLimit = "20"
        testSubject.triggerCreateGameEvent("username", timeLimit, arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.TIME_LIMIT_ERROR))
    }
    //TODO find a way to test with 0 as time. Currently the Build.Debug being true hurts this

    @Test
    fun `given all params correct, when backend is succesful, then succesfully create game and post to create game event`()= runBlockingTest {
        val username = "this is me"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forResult(false))
        whenever(gameService.getLocationsFromPack(any(), any())).thenReturn(Tasks.forResult(listOf("this", "and", "that")))
        whenever(gameService.setGame(any(), any())).thenReturn(getVoidTask())
        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.data?.currentUser, equalTo(username))
    }

    @Test
    fun `given all params correct and game service successful, when access code taken, then still succesfully create game and post to create game event`()= runBlockingTest {
        val username = "this is me"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forResult(true), Tasks.forResult(false))
        whenever(gameService.getLocationsFromPack(any(), any())).thenReturn(Tasks.forResult(listOf("this", "and", "that")))
        whenever(gameService.setGame(any(), any())).thenReturn(getVoidTask())

        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.data?.currentUser, equalTo(username))
    }

    @Test
    fun `given everything goes smoothly, when create game is triggered, then ensure save session is called`() = runBlockingTest {
        val username = "username"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forResult(false))
        whenever(gameService.getLocationsFromPack(any(), any())).thenReturn(Tasks.forResult(listOf("this", "and", "that")))
        whenever(gameService.setGame(any(), any())).thenReturn(getVoidTask())

        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        verify(prefHelper, times(1)).saveSession(argWhere { it.currentUser == username })
    }

    @Test
    fun `given all params correct, when backend error durring generate acces code then post error to create game event`() = runBlockingTest {
        val username = "this is me"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forException(Exception()))

        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.UNKNOWN_ERROR))
    }

    @Test
    fun `given all params correct, when backend error durring get locations, then post error to create game event`() = runBlockingTest {
        val username = "this is me"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forResult(false))
        whenever(gameService.getLocationsFromPack(any(), any())).thenReturn(Tasks.forException(
            Exception()
        ))

        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.UNKNOWN_ERROR))
    }

    @Test
    fun `given all params correct, when backend error durring set game, then post error to create game event`() = runBlockingTest {
        val username = "this is me"
        whenever(gameService.accessCodeExists(any())).thenReturn(Tasks.forResult(false))
        whenever(gameService.getLocationsFromPack(any(), any())).thenReturn(Tasks.forResult(listOf("this", "and", "that")))
        whenever(gameService.setGame(any(), any())).thenReturn(Tasks.forException(Exception()))

        testSubject.triggerCreateGameEvent(username, "1", arrayListOf("Something") )
        val value = testSubject.getCreateGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NewGameError.UNKNOWN_ERROR))
    }


    ///////GET PACKS DETAILS////////////
    @Test
    fun `Given the backend returns null list, when get packs details is called, then error should be posted to event`() {
        whenever(gameService.getPackDetails()).thenReturn(Tasks.forResult(null))
        testSubject.triggerGetPackDetailsEvent()
        val value = testSubject.getShowPackEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(PackDetailsError.UNKNOWN_ERROR))
    }

    @Test
    fun `Given the backend returns empty list, when get packs details is called, then error should be posted to event`() {
        whenever(gameService.getPackDetails()).thenReturn(Tasks.forResult(listOf()))
        testSubject.triggerGetPackDetailsEvent()
        val value = testSubject.getShowPackEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(PackDetailsError.UNKNOWN_ERROR))
    }

    @Test
    fun `Given the backend returns not full list, when get packs details is called, then error should be posted to event`() {
        val testList = listOf(
            listOf("1","2", "3", "4", "5"),
            listOf("1","2", "3", "4", "5")
        )
        whenever(gameService.getPackDetails()).thenReturn(Tasks.forResult(testList))
        testSubject.triggerGetPackDetailsEvent()
        val value = testSubject.getShowPackEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(PackDetailsError.UNKNOWN_ERROR))
    }

    @Test
    fun `Given the backend returns full list, when get packs details is called, then success should be posted to event`() {
        val testList = listOf(
            listOf("1","2", "3", "4", "5"),
            listOf("1","2", "3", "4", "5"),
            listOf("1","2", "3", "4", "5")
            )
        whenever(gameService.getPackDetails()).thenReturn(Tasks.forResult(testList))
        testSubject.triggerGetPackDetailsEvent()
        val value = testSubject.getShowPackEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test
    fun `Given the backend failure, when get packs details is called, then error should be posted to event`() {
        whenever(gameService.getPackDetails()).thenReturn(Tasks.forException(Exception()))
        testSubject.triggerGetPackDetailsEvent()
        val value = testSubject.getShowPackEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Error)
    }

}