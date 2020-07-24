package com.dangerfield.spyfall.ui.joinGame

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.di.TestKoinApp
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.game.GameViewModel
import com.dangerfield.spyfall.util.ConnectivityHelper
import com.dangerfield.spyfall.util.GameService
import com.dangerfield.spyfall.util.PreferencesService
import com.dangerfield.spyfall.util.SessionListenerService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config

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

    @Before
    fun setUp() {
        testSubject = JoinGameViewModel(testRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}