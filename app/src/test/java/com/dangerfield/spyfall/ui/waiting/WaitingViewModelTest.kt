package com.dangerfield.spyfall.ui.waiting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.getOrAwaitValue
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.StartGameError
import com.dangerfield.spyfall.util.Event
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

@RunWith(AndroidJUnit4::class)
@Config(application = TestKoinApp::class, sdk = [28])
class WaitingViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val mockRepo: GameRepository = Mockito.mock(GameRepository::class.java)
    private lateinit var testSubject: WaitingViewModel

    @After
    fun stopKoinAfterTest() = stopKoin()

    @Before fun setupViewModel() {
        val currentUsername = "ABC123"
        val game = Game.getEmptyGame().copy(playerList = arrayListOf(currentUsername))
        val session = Session("",currentUsername, game)
        testSubject = WaitingViewModel(mockRepo, session)
    }

    @Test
    fun triggerChangeName__firesNameChangeEventWithNewName() {
        //given the repository returns a succesful name change
        val newName = "Patrick Star"
        Mockito.`when`(mockRepo.changeName(newName, testSubject.currentSession)).thenReturn(MutableLiveData(Event(Resource.Success(newName))))

        // when name change is triggered
        testSubject.triggerChangeNameEvent(newName)

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.data, equalTo(newName))
    }

    @Test
    fun triggerChangeName_startedGame_firesNameChangeEventWithGameStartedErrorError() {
        //given a view model with a started game
        testSubject.currentSession.game.started = true
        // when name change is triggered
        testSubject.triggerChangeNameEvent("Some new name")

        //assert that the name change event is fired with a game started error
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.GAME_STARTED))
    }

    @Test
    fun triggerChangeName__repositoryError_firesNameChangeEventWithUnknownError() {
        //given the repository returns an unknown error on name changes
        val repoResult : LiveData<Event<Resource<String, NameChangeError>>> = MutableLiveData(Event(Resource.Error(error = NameChangeError.UNKNOWN_ERROR)))
        Mockito.`when`(mockRepo.changeName("New Name", testSubject.currentSession)).thenReturn(repoResult)

        // when name change is triggered
        testSubject.triggerChangeNameEvent("New Name")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.UNKNOWN_ERROR))
    }

    @Test
    fun triggerChangeName__emptyString_firesNameChangeEventWithFormatError() {
        // when name change is triggered
        testSubject.triggerChangeNameEvent("")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.FORMAT_ERROR))
    }

    @Test
    fun triggerChangeName__26Chars_firesNameChangeEventWithFormatError() {
        // when name change is triggered
        testSubject.triggerChangeNameEvent("aaaaaaaaaaaaaaaaaaaaaaaaaa")

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.FORMAT_ERROR))
    }

    @Test
    fun triggerChangeName_sameName_firesNameChangeEventWithNameInUsError() {
        // when name change is triggered
        testSubject.triggerChangeNameEvent(testSubject.currentSession.currentUser)

        //assert that the name change event is fired with the new name given from the repository
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.NAME_IN_USE))
    }

    @Test
    fun triggerChangeName_takenName_firesNameChangeEventWithNameInUseError() {
        //given a session with a particular name
        val takenName = "this is a taken name"
        testSubject.currentSession.game = testSubject.currentSession.game.copy(playerList = arrayListOf(testSubject.currentSession.currentUser, takenName))
        // when name change is triggered with that name as the new name
        testSubject.triggerChangeNameEvent(takenName)
        //assert that the name change event is fired with the error of name in use
        val value = testSubject.getNameChangeEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(NameChangeError.NAME_IN_USE))
    }

    @Test
    fun triggerStartGame_startedGame_firesstartGameEventWithGameStartedErrorError() {
        //given a view model with a started game
        testSubject.currentSession.game.started = true
        // when start game is triggered
        testSubject.triggerStartGameEvent()
        //assert that the start game event is fired with a game started error
        val value = testSubject.getStartGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.GAME_STARTED))
    }

    @Test
    fun triggerStartGame_noError_firesStartGameEventEvent() {
        //given a repo that returns success
        Mockito.`when`(mockRepo.startGame(testSubject.currentSession)).thenReturn(MutableLiveData(Event(Resource.Success(Unit))))
        // when start game is triggered
        testSubject.triggerStartGameEvent()
        //assert that the start game event is fired with success
        val value = testSubject.getStartGameEvent().getOrAwaitValue()
        assert(value?.getContentIfNotHandled() is Resource.Success)
    }

    @Test
    fun triggerStartGame_unknownError_firesStartGameEventEventWithUnknownError() {
        //given a repo that returns unknown error
        Mockito.`when`(mockRepo.startGame(testSubject.currentSession)).thenReturn(MutableLiveData(Event(Resource.Error(error = StartGameError.Unknown))))
        // when start game is triggered
        testSubject.triggerStartGameEvent()
        //assert that the start game event is fired with unknown error
        val value = testSubject.getStartGameEvent().getOrAwaitValue()
        assertThat(value?.getContentIfNotHandled()?.error, equalTo(StartGameError.Unknown))
    }
}