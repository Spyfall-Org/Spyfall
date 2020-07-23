package com.dangerfield.spyfall.ui.waiting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.getOrAwaitValue
import com.dangerfield.spyfall.getVoidTask
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.util.*
import com.google.android.gms.tasks.Tasks
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.lang.Exception
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestKoinApp::class, sdk = [28])
class WaitingViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val gameService: GameService = Mockito.mock(GameService::class.java)
    private val prefHelper: PreferencesService = Mockito.mock(PreferencesService::class.java)
    private val sessionListener: SessionListenerService =
        Mockito.mock(SessionListenerService::class.java)

    private val testDispatcher = TestCoroutineDispatcher()
    private val testRepository = Repository(gameService, sessionListener, prefHelper, testDispatcher)
    private val mockRepo: GameRepository = Mockito.mock(GameRepository::class.java)
    private lateinit var testSubjectMockRepo: WaitingViewModel
    private lateinit var testSubjectRealRepo: WaitingViewModel



    @After
    fun stopKoinAfterTest() = stopKoin()

    @Before
    fun setupViewModel() {
        val currentUsername = "ABC123"
        val game = Game.getEmptyGame().copy(playerList = arrayListOf(currentUsername))
        val session = Session("", currentUsername, game)
        testSubjectMockRepo = WaitingViewModel(mockRepo, session)
        testSubjectRealRepo = WaitingViewModel(testRepository, session)
    }

    //________________________TESTS INDEPENDENT OF REPO___________________________//
    @Test
    fun triggerChangeName__firesNameChangeEventWithNewName() {
        //given the repository returns a succesful name change
        val newName = "Patrick Star"
        Mockito.`when`(mockRepo.changeName(newName, testSubjectMockRepo.currentSession))
            .thenReturn(MutableLiveData(Event(Resource.Success(newName))))

        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent(newName)

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.data, equalTo(newName))
    }

    @Test
    fun triggerChangeName_startedGame_firesNameChangeEventWithGameStartedError() {
        //given a view model with a started game
        testSubjectMockRepo.currentSession.game.started = true
        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent("Some new name")

        //assert that the name change event is fired with a game started error
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.GAME_STARTED))
    }

    @Test
    fun triggerChangeName__repositoryError_firesNameChangeEventWithUnknownError() {
        //given the repository returns an unknown error on name changes
        val repoResult: LiveData<Event<Resource<String, NameChangeError>>> =
            MutableLiveData(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
        Mockito.`when`(mockRepo.changeName("New Name", testSubjectMockRepo.currentSession))
            .thenReturn(repoResult)

        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent("New Name")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.UNKNOWN_ERROR))
    }

    @Test
    fun triggerChangeName__emptyString_firesNameChangeEventWithFormatError() {
        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent("")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.FORMAT_ERROR))
    }

    @Test
    fun triggerChangeName__26Chars_firesNameChangeEventWithFormatError() {
        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent("aaaaaaaaaaaaaaaaaaaaaaaaaa")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.FORMAT_ERROR))
    }

    @Test
    fun triggerChangeName_sameName_firesNameChangeEventWithNameInUsError() {
        // when name change is triggered
        testSubjectMockRepo.triggerChangeNameEvent(testSubjectMockRepo.currentSession.currentUser)

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.NAME_IN_USE))
    }

    @Test
    fun triggerChangeName_takenName_firesNameChangeEventWithNameInUseError() {
        //given a session with a particular name
        val takenName = "this is a taken name"
        testSubjectMockRepo.currentSession.game = testSubjectMockRepo.currentSession.game.copy(
            playerList = arrayListOf(
                testSubjectMockRepo.currentSession.currentUser,
                takenName
            )
        )
        // when name change is triggered with that name as the new name
        testSubjectMockRepo.triggerChangeNameEvent(takenName)
        //assert that the name change event is fired with the error of name in use
        val value = testSubjectMockRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.NAME_IN_USE))
    }

    @Test
    fun triggerStartGame_startedGame_firesstartGameEventWithGameStartedError() {
        //given a view model with a started game
        testSubjectMockRepo.currentSession.game.started = true
        // when start game is triggered
        testSubjectMockRepo.triggerStartGameEvent()
        //assert that the start game event is fired with a game started error
        val value = testSubjectMockRepo.getStartGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.GAME_STARTED))
    }

    @Test
    fun triggerStartGame_noError_firesStartGameEvent() {
        //given a repo that returns success
        Mockito.`when`(mockRepo.startGame(testSubjectMockRepo.currentSession))
            .thenReturn(MutableLiveData(Event(Resource.Success(Unit))))
        // when start game is triggered
        testSubjectMockRepo.triggerStartGameEvent()
        //assert that the start game event is fired with success
        val value = testSubjectMockRepo.getStartGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test
    fun triggerStartGame_unknownError_firesStartGameEventEventWithUnknownError() {
        //given a repo that returns unknown error
        Mockito.`when`(mockRepo.startGame(testSubjectMockRepo.currentSession))
            .thenReturn(MutableLiveData(Event(Resource.Error(error = StartGameError.Unknown))))
        // when start game is triggered
        testSubjectMockRepo.triggerStartGameEvent()
        //assert that the start game event is fired with unknown error
        val value = testSubjectMockRepo.getStartGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown))
    }


    //________________________TESTS DEPENDENT ON REPO___________________________//

    ///LEAVE GAME///
    @Test
    fun triggerLeaveGame_multiplePlayers_firesLeavetGameEvent() {
        //given a game with two players and game service is successful
        testSubjectRealRepo.currentSession.game.playerList.add("Second player")
        Mockito.`when`(
            gameService.removePlayer(
                testSubjectRealRepo.currentSession.accessCode,
                testSubjectRealRepo.currentSession.currentUser
            )
        )
            .thenReturn(getVoidTask())

        //when a player leaves a game
        testSubjectRealRepo.triggerLeaveGameEvent()

        //assert that the start game event is fired with success
        val value = testSubjectRealRepo.getLeaveGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test(expected = TimeoutException::class)
    fun triggerLeaveGame_singlePlayer_doesNotFireLeaveGameEvent() = runBlockingTest {
        //given a game with one player(default in tests) and game service is successful
        Mockito.`when`(
            gameService.removePlayer(
                testSubjectRealRepo.currentSession.accessCode,
                testSubjectRealRepo.currentSession.currentUser
            )
        )
            .thenReturn(getVoidTask())

        //when a player leaves a game
        testSubjectRealRepo.triggerLeaveGameEvent()

        //assert that the get leave game event wont be posted to and will thow a TIMEOUT EXCEPTION
        testSubjectRealRepo.getLeaveGameEvent().getOrAwaitValue()
    }

    @Test
    fun triggerLeaveGame_success_removesSavedSession() = runBlockingTest {
        //given a game with 2 players and a game service that is successful
        testSubjectRealRepo.currentSession.game.playerList.add("Second player")
        Mockito.`when`(
            gameService.removePlayer(
                testSubjectRealRepo.currentSession.accessCode,
                testSubjectRealRepo.currentSession.currentUser
            )
        )
            .thenReturn(getVoidTask())

        //when a player leaves a game
        testSubjectRealRepo.triggerLeaveGameEvent()

        //verify that the saved session will be removed
        verify(prefHelper, times(1)).removeSavedSession()
    }

    @Test
    fun triggerLeaveGame_success_removesGameListener() = runBlockingTest {
        //given a game with 2 players and a game service that is successful
        testSubjectRealRepo.currentSession.game.playerList.add("Second player")
        Mockito.`when`(
            gameService.removePlayer(
                testSubjectRealRepo.currentSession.accessCode,
                testSubjectRealRepo.currentSession.currentUser
            )
        )
            .thenReturn(getVoidTask())

        //when a player leaves a game
        testSubjectRealRepo.triggerLeaveGameEvent()

        //verify that the saved session will be removed
        verify(sessionListener, times(1)).removeListener()
    }



    @Test
    fun triggerLeaveGame_gameServiceError_firesLeavetGameEventWithUnknownError() {
        //given the game service has a failure
        Mockito.`when`(
            gameService.removePlayer(
                testSubjectRealRepo.currentSession.accessCode,
                testSubjectRealRepo.currentSession.currentUser
            )
        )
            .thenReturn(Tasks.forException(Exception()))

        //when a player leaves a game
        testSubjectRealRepo.triggerLeaveGameEvent()

        //assert that the leave game event is fired with error
        val value = testSubjectRealRepo.getLeaveGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(LeaveGameError.UNKNOWN_ERROR))
    }

    ///CHANGE NAME///

    @Test
    fun triggerChangeName_userNotInPlayerList_firesNameChangeEventWithUnkownError() {
        //given a view model with a playerlist

        // when name change is triggered with a player not in the last
        testSubjectRealRepo.currentSession.currentUser = "a name that isnt in the game"
        testSubjectRealRepo.triggerChangeNameEvent("new name")

        //assert that the name cant be found and the repo returns unknown error
        val value = testSubjectRealRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.UNKNOWN_ERROR))
    }

    @Test
    fun triggerChangeName_success_PostsSuccessWithNewName() = runBlockingTest {
        val newName = "new name"
        //given the game service is successfully contacted
        Mockito.`when`(gameService.setPlayerList(any(), any()))
            .thenReturn(getVoidTask())
        // when name change is triggered
        testSubjectRealRepo.triggerChangeNameEvent(newName)

        //then asser that the name change event is fired with new name
        val value = testSubjectRealRepo.getNameChangeEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled()?.data == newName)
    }

    @Test
    fun triggerChangeName_success_calledSaveSessionWithNewName() = runBlockingTest {
        val newName = "new name"
        //given the game service is successfully contacted
        Mockito.`when`(gameService.setPlayerList(any(),any()))
            .thenReturn(getVoidTask())
        // when name change is triggered
        testSubjectRealRepo.triggerChangeNameEvent(newName)

       verify(prefHelper, times(1)).saveSession(argWhere { it.currentUser == newName })
    }

    @Test
    fun triggerChangeName_failure_returnsUnknownError() = runBlockingTest {
        //given the game service fails
        Mockito.`when`(gameService.setPlayerList(any(),any()))
            .thenReturn(Tasks.forException(Exception()))
        // when name change is triggered
        testSubjectRealRepo.triggerChangeNameEvent("new name")

        val value = testSubjectRealRepo.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.UNKNOWN_ERROR))
    }

    //START GAME///

    //TODO find a way to test the getting roles function, as it is not currently testable
    /*
    This blocks you from testin
    1. that increment games played gets called
    2. that there are different results based on what firebase returns for the roles (null, empty, failure, real results)
     */
    @Test
    fun triggerStartGame_firestoreErrorSettingPlayerObjects_postsUnknownError() = runBlockingTest {
        //given that firestore fails when pushing player objects
        Mockito.`when`(gameService.setPlayerObjectsList(any(), any()))
            .thenReturn(Tasks.forException(Exception()))
        // when name start game is triggered
        testSubjectRealRepo.triggerStartGameEvent()

        //assert that an unknown error is posted to the start game event
        val value = testSubjectRealRepo.getStartGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown))
    }

}