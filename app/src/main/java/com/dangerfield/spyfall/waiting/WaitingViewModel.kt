package com.dangerfield.spyfall.waiting

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.util.Event

enum class NameChangeError(val resId: Int) {
    FORMAT_ERROR(R.string.change_name_character_limit),
    NAME_IN_USE(R.string.change_name_different_name),
    NETWORK_ERROR(R.string.newtork_error_message),
    GAME_STARTED(R.string.change_name_error_started_game),
    UNKNOWN_ERROR(R.string.unknown_error)
}

class WaitingViewModel(private val repository: GameRepository) : ViewModel() {
    val game = repository.currentSession?.getLiveGame()
    val accessCode: String = repository.currentSession?.accessCode.orEmpty()
    val gameExists = repository.currentSession?.getGameExists()

    private val nameChangeEvent: MediatorLiveData<Event<Resource<String, NameChangeError>>> =
        MediatorLiveData()

    fun getNameChangeEvent() = nameChangeEvent

    fun getCurrentUser() = repository.currentSession?.currentUser.orEmpty()

    fun leaveGame() =
        repository.leaveGame()

    fun startGame() =
        repository.startGame()

    fun fireNameChange(newName: String) {
        if (foundErrors(newName)) return
        nameChangeEvent.addSource(repository.changeName(newName)) {
            nameChangeEvent.value = it
        }
    }

    private fun foundErrors(newName: String): Boolean {
        when {
            newName.length > 25 ||
                    newName.isEmpty() -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.FORMAT_ERROR))
            repository.currentSession?.getGameValue()?.playerList?.contains(newName) == true -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.NAME_IN_USE))
            repository.currentSession?.gameHasBegun() == true -> nameChangeEvent.value =
                Event(Resource.Error(error = NameChangeError.GAME_STARTED))
            else -> {
                return false
            }
        }
        return true
    }
}