package com.dangerfield.spyfall.ui.newGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session

 enum class NewGameError(val resId: Int? = null) {
    NO_SELECTED_PACK(R.string.new_game_error_select_pack),
    EMPTY_NAME(R.string.new_game_string_error_name),
    NAME_CHARACTER_LIMIT(R.string.change_name_character_limit),
    TIME_LIMIT_ERROR(R.string.new_game_error_time_limit),
    NETWORK_ERROR(),
    UNKNOWN_ERROR(R.string.unknown_error)
}

enum class PackDetailsError(val resId: Int? = null) {
    NETWORK_ERROR,
    UNKNOWN_ERROR(R.string.unknown_error)
}
class NewGameViewModel(private val repository: GameRepository) : ViewModel() {

    private var packsDetails = MutableLiveData<Resource<List<List<String>>, PackDetailsError>>()

    fun cancelPendingOperations() =repository.cancelJobs()

    fun createGame(username: String, timeLimit: String, selectedPacks: ArrayList<String>): LiveData<Resource<Session, NewGameError>> {
        var result = MutableLiveData<Resource<Session, NewGameError>>()

        when {
            selectedPacks.isEmpty() ->  result.value = Resource.Error(error = NewGameError.NO_SELECTED_PACK)

            username.isEmpty() ->  result.value = Resource.Error(error = NewGameError.EMPTY_NAME)

            username.length > 25 ->  result.value = Resource.Error(error = NewGameError.NAME_CHARACTER_LIMIT)

            timeLimit.isEmpty() || timeLimit.toInt() > 10 || timeLimit.toInt() == 0 ->
                result.value = Resource.Error(error = NewGameError.TIME_LIMIT_ERROR)

            else -> result = repository.createGame(username, timeLimit.toLong(), selectedPacks) as MutableLiveData<Resource<Session, NewGameError>>
        }

        return result
    }

    fun getPacksDetails(): MutableLiveData<Resource<List<List<String>>, PackDetailsError>> {
        if(packsDetails.value?.data.isNullOrEmpty()){
            packsDetails = repository.getPacksDetails() as MutableLiveData<Resource<List<List<String>>, PackDetailsError>>
        }
        return packsDetails
    }

    fun getPacks() = repository.getPacks()

}