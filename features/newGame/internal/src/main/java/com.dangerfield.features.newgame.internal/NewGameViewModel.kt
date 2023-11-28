package com.dangerfield.features.newgame.internal

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.Pack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    gameConfig: GameConfig,
    private val locationPackRepository: LocationPackRepository,
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)
    private val _events = Channel<Event>()

    val events = _events.receiveAsFlow()

    init {
        actions.trySend(Action.LoadPacks)
    }

    val state = flow {
        for (action in actions) handleAction(action)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        State(
            packs = emptyList(),
            gameType = GameType.MultiDevice,
            timeLimit = "",
            name = "",
            isLoadingCreation = false,
            isLoadingPacks = true,
            maxPlayers = gameConfig.maxPlayers
        )
    )

    fun updateName(name: String) = actions.trySend(Action.UpdateName(name))
    fun updateTimeLimit(timeLimit: String) = actions.trySend(Action.UpdateTimeLimit(timeLimit))
    fun updateGameType(isSingleDevice: Boolean) =
        actions.trySend(Action.UpdateGameType(isSingleDevice))

    fun updateNumOfPlayers(numOfPlayers: String) =
        actions.trySend(Action.UpdateNumOfPlayers(numOfPlayers))

    fun selectPack(pack: DisplayPack, isSelected: Boolean) =
        actions.trySend(Action.SelectPack(pack, isSelected))

    fun createGame() = actions.trySend(Action.CreateGame)

    private suspend fun FlowCollector<State>.handleAction(action: Action) {
        when (action) {
            is Action.LoadPacks -> handleLoadPacks()
            is Action.CreateGame -> handleCreateGame()
            is Action.UpdateName -> handleUpdateName(action.name)
            is Action.UpdateTimeLimit -> handleUpdateTimeLimit(action.timeLimit)
            is Action.UpdateGameType -> handleUpdateGameType(action.isSingleDevice)
            is Action.UpdateNumOfPlayers -> handleUpdateNumOfPlayers(action.numOfPlayers)
            is Action.SelectPack -> handleSelectPack(action.pack, action.isSelected)
        }
    }

    private suspend fun FlowCollector<State>.handleCreateGame() {
        updateState { it.copy(isLoadingCreation = true) }
        // validate all fields
        // call use case to create the game
        // emit state based on the result
        updateState { it.copy(isLoadingCreation = false) }
    }

    private suspend fun FlowCollector<State>.handleUpdateName(name: String) {
        updateState { it.copy(name = name) }
    }

    private suspend fun FlowCollector<State>.handleUpdateGameType(isSingleDevice: Boolean) {
        val type = if (isSingleDevice) {
            GameType.SingleDevice("")
        } else {
            GameType.MultiDevice
        }
        updateState { it.copy(gameType = type) }
    }

    private suspend fun FlowCollector<State>.handleUpdateNumOfPlayers(numOfPlayers: String) {
        if (state.value.gameType !is GameType.SingleDevice) {
            Timber.e("Number of players was updated but game type is not single device")
        } else {
            updateState {
                it.copy(gameType = GameType.SingleDevice(numOfPlayers))
            }
        }
    }

    private suspend fun FlowCollector<State>.handleUpdateTimeLimit(timeLimit: String) {
        updateState { it.copy(timeLimit = timeLimit) }
    }

    private suspend fun FlowCollector<State>.handleSelectPack(
        pack: DisplayPack,
        isSelected: Boolean
    ) {
        updateState {
            val updatedPacks = it.packs.map { p ->
                if (p.key == pack.key) {
                    p.copy(isSelected = isSelected)
                } else {
                    p
                }
            }
            it.copy(packs = updatedPacks)
        }
    }

    private suspend fun FlowCollector<State>.handleLoadPacks() = locationPackRepository
        .getPacks()
        .map { packs ->
            packs.map { it.toDisplayPack() }

        }.onSuccess { packs ->
            updateState { it.copy(packs = packs, isLoadingPacks = false) }
        }.onFailure {
            updateState { it.copy(didSomethingGoWrong = true, isLoadingPacks = false) }
        }

    private suspend fun FlowCollector<State>.updateState(update: (State) -> State) {
        val currentValue = state.value
        val nextValue = update(currentValue)
        emit(nextValue)
    }

    private fun Pack.toDisplayPack() = DisplayPack(
        key = name,
        number = name.split(" ").first { it.isDigitsOnly() },
        type = name.split(" ").filter { !it.isDigitsOnly() }.joinToString(" "),
        isSelected = false
    )

    sealed class Action {
        class UpdateName(val name: String) : Action()
        class UpdateTimeLimit(val timeLimit: String) : Action()
        class UpdateGameType(val isSingleDevice: Boolean) : Action()
        class UpdateNumOfPlayers(val numOfPlayers: String) : Action()
        class SelectPack(val pack: DisplayPack, val isSelected: Boolean) : Action()
        data object CreateGame : Action()
        data object LoadPacks : Action()
    }

    sealed class Event {
        data object GameCreated : Event()
    }

    data class State(
        val packs: List<DisplayPack>,
        val gameType: GameType = GameType.MultiDevice,
        val timeLimit: String,
        val name: String,
        val maxPlayers: Int,
        val isLoadingPacks: Boolean,
        val isLoadingCreation: Boolean,
        val didSomethingGoWrong: Boolean = false
    )

    sealed class GameType {
        class SingleDevice(
            val numberOfPlayers: String,
        ) : GameType()

        data object MultiDevice : GameType()
    }
}