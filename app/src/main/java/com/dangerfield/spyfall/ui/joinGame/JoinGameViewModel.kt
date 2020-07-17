package com.dangerfield.spyfall.ui.joinGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session

enum class JoinGameError(val resId: Int? = null)  {
    FIELD_ERROR(R.string.join_game_error_fields),
    GAME_DOES_NOT_EXIST(R.string.join_game_error_access_code),
    GAME_HAS_MAX_PLAYERS(R.string.join_game_error_max_players),
    GAME_HAS_STARTED(R.string.join_game_error_started_game),
    NAME_TAKEN(R.string.join_game_error_taken_name),
    NAME_CHARACTER_LIMIT(R.string.change_name_character_limit),
    COULD_NOT_JOIN(R.string.join_game_error_could_not_join),
    UNKNOWN_ERROR(R.string.unknown_error),
    NETWORK_ERROR
}
class JoinGameViewModel(private val repository: GameRepository) : ViewModel() {

    fun joinGame(accessCode: String, username: String): LiveData<Resource<Session, JoinGameError>> {
        var result = MutableLiveData<Resource<Session, JoinGameError>>()

        if(username.isEmpty() || accessCode.isEmpty()){
            result.value =  Resource.Error(error = JoinGameError.FIELD_ERROR)
        } else if(username.length > 25) {
            result.value = Resource.Error(error = JoinGameError.NAME_CHARACTER_LIMIT)
        } else result = repository.joinGame(accessCode, username) as MutableLiveData<Resource<Session, JoinGameError>>

        return result
    }
}