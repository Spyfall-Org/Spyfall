package com.dangerfield.spyfall.joinGame

import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Repository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.util.Event

enum class JoinGameError {
    FIELD_ERROR,
    NETWORK_ERROR,
    GAME_DOES_NOT_EXIST,
    GAME_HAS_MAX_PLAYERS,
    GAME_HAS_STARTED,
    NAME_TAKEN,
    NAME_CHARACTER_LIMIT,
    COULD_NOT_JOIN,
    UNKNOWN_ERROR
}
class JoinGameViewModel(private val repository: GameRepository) : ViewModel() {

    fun joinGame(accessCode: String, username: String): LiveData<Resource<Unit, JoinGameError>> {
        var result = MutableLiveData<Resource<Unit, JoinGameError>>()

        if(username.isEmpty() || accessCode.isEmpty()){
            result.value =  Resource.Error(error = JoinGameError.FIELD_ERROR)
        } else if(username.length > 25) {
            result.value = Resource.Error(error = JoinGameError.NAME_CHARACTER_LIMIT)
        } else result = repository.joinGame(accessCode, username) as MutableLiveData<Resource<Unit, JoinGameError>>

        return result
    }
}