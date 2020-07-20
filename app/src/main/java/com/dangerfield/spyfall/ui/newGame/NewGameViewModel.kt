package com.dangerfield.spyfall.ui.newGame

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.GameRepository
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.util.Event

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

    private var showPackEvent = MediatorLiveData<Event<Resource<List<List<String>>, PackDetailsError>>>()
    private var createGameEvent = MediatorLiveData<Event<Resource<Session, NewGameError>>>()
    private var packsDetails : List<List<String>>? = null

    fun getShowPackEvent() = showPackEvent
    fun getCreateGameEvent() = createGameEvent
    fun getPacks() = repository.getPacks()
    fun cancelPendingOperations() = repository.cancelCreateGame()

    fun triggerCreateGameEvent(
        username: String,
        timeLimit: String,
        selectedPacks: ArrayList<String>
    ) {
        val error = findCreateGameError(username, timeLimit, selectedPacks)
        if(error != null) {
            createGameEvent.postValue(Event(Resource.Error(error = error)))
        } else {
            val repoResult = repository.createGame(username, timeLimit.toLong(), selectedPacks)
            createGameEvent.addSource(repoResult) {
                createGameEvent.postValue(Event(it))
                createGameEvent.removeSource(repoResult)
            }
        }
    }

    fun triggerGetPackDetailsEvent() {
        if(packsDetails.isNullOrEmpty()){
            val repoResult = repository.getPacksDetails()
            showPackEvent.addSource(repoResult) {
                it.data?.let {d -> packsDetails = d }
                showPackEvent.postValue(Event(it))
                showPackEvent.removeSource(repoResult)
            }
        } else {
            packsDetails?.let { showPackEvent.postValue(Event(Resource.Success(it))) }
        }
    }

    private fun findCreateGameError(username: String,
                                    timeLimit: String,
                                    selectedPacks: ArrayList<String>): NewGameError? {
        return when {
            selectedPacks.isEmpty() -> NewGameError.NO_SELECTED_PACK
            username.isEmpty() ->  NewGameError.EMPTY_NAME
            username.length > 25 ->  NewGameError.NAME_CHARACTER_LIMIT
            timeLimit.isEmpty() || timeLimit.toInt() > 10 || zeroTimeCheck(timeLimit.toInt()) ->
                NewGameError.TIME_LIMIT_ERROR
            else -> null
        }
    }

    //in debug I want to be able to use zero
    private fun zeroTimeCheck(time: Int) =
        !BuildConfig.DEBUG && time == 0

}