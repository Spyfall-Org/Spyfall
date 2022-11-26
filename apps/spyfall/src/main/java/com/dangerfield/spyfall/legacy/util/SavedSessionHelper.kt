package com.dangerfield.spyfall.legacy.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.legacy.api.GameService
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.models.Game
import com.dangerfield.spyfall.legacy.ui.start.SavedSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

interface EpochGetter {
    fun getCurrentEpoch(): Long
}

class SavedSessionHelper(
    private val preferencesHelper: PreferencesService,
    private val gameService: GameService,
    private val dispatcher: CoroutineDispatcher = IO,
    private val epochGetter: EpochGetter = object : EpochGetter {
        override fun getCurrentEpoch() = System.currentTimeMillis() / 1000
    }
) {
    /**
     * Checks preferences for a saved game
     * if that game is still on firebase we assume the user can still join it
     */
    fun findUserInExistingGame(): LiveData<Resource<SavedSession, Unit>> {
        val result = MutableLiveData<Resource<SavedSession, Unit>>()
        CoroutineScope(dispatcher).launch {
            preferencesHelper.getSavedSession()?.let { session ->
                try {
                    val game = gameService.getGame(session.accessCode).await()
                    if (game != null && userCanEnterGame(game, session)) {
                        result.postValue(Resource.Success(SavedSession(session, game.started)))
                    } else {
                        result.postValue(Resource.Error(error = Unit))
                    }
                } catch (e: Exception) {
                    if (!BuildConfig.DEBUG) LogHelper.logErrorWhenCheckingIfUserisAlreadyInGame()
                    result.postValue(Resource.Error(error = Unit))
                }
            } ?: result.postValue(Resource.Error(error = Unit))
        }
        return result
    }

    private fun userCanEnterGame(game: Game, currentSession: Session): Boolean {
        return (!gameIsExpired(game)
                && (game.playerList.contains(currentSession.currentUser) || game.playerList.contains(
            currentSession.previousUserName
        ))
                && (!gameIsStarted(game) || userCanEnterStartedGame(game, currentSession))
                )
    }

    private fun userCanEnterStartedGame(game: Game, currentSession: Session): Boolean = (
            (game.playerObjectList.find { it.username == currentSession.currentUser } != null
                    || game.playerObjectList.find { it.username == currentSession.previousUserName } != null)
            )


    private fun gameIsExpired(game: Game): Boolean {
        game.expiration.let { expiration ->
            val now = epochGetter.getCurrentEpoch()
            return expiration <= now
        }
    }

    private fun gameIsStarted(game: Game) = game.playerObjectList.size > 0
}