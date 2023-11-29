package com.dangerfield.features.newgame.internal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.newgame.internal.presentation.model.Action
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.FieldState
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Idle
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Invalid
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Valid
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.internal.presentation.model.GameType.MultiDevice
import com.dangerfield.features.newgame.internal.presentation.model.GameType.SingleDevice
import com.dangerfield.features.newgame.internal.presentation.model.State
import com.dangerfield.features.newgame.internal.presentation.model.numberOfPlayers
import com.dangerfield.features.newgame.internal.presentation.model.selectedPacks
import com.dangerfield.features.newgame.internal.presentation.model.timeLimit
import com.dangerfield.features.newgame.internal.presentation.model.userName
import com.dangerfield.features.newgame.internal.usecase.CreateGame
import com.dangerfield.features.newgame.internal.usecase.CreateSingleDeviceGame
import com.dangerfield.libraries.coreflowroutines.launchOnStart
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.LocationPackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import spyfallx.core.Try
import spyfallx.core.allOrNone
import spyfallx.core.checkInDebug
import spyfallx.core.illegalState
import spyfallx.core.logOnError
import spyfallx.core.throwIfDebug
import spyfallx.core.withBackoffRetry
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    private val locationPackRepository: LocationPackRepository,
    private val createMultiDeviceGameGameUseCase: CreateGame,
    private val createSingleDeviceGameUseCase: CreateSingleDeviceGame,
    private val gameConfig: GameConfig
) : ViewModel() {

    private val actions = Channel<Action>(Channel.UNLIMITED)
    private val _events = Channel<Event>()

    val events = _events.receiveAsFlow()

    val state = flow {
        for (action in actions) handleAction(action)
    }
        .map { it.withFormValidation() }
        .launchOnStart { loadPacks() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            State(
                packsState = Idle(emptyList()),
                timeLimitState = Valid("8"),
                nameState = Idle(""),
                videoCallLink = "",
                isLoadingPacks = true,
                isLoadingCreation = false,
                didSomethingGoWrong = false,
                formState = FormState.Idle,
                isSingleDevice = false,
                numberOfPlayersState = Idle(""),
            )
        )

    fun updateGameType(isSingleDevice: Boolean) =
        actions.trySend(Action.UpdateGameType(isSingleDevice))

    fun updateNumOfPlayers(numOfPlayers: String) =
        actions.trySend(Action.UpdateNumOfPlayers(numOfPlayers))

    fun selectPack(pack: DisplayablePack, isSelected: Boolean) =
        actions.trySend(Action.SelectPack(pack, isSelected))

    fun createGame() = actions.trySend(Action.CreateGame)
    fun loadPacks() = actions.trySend(Action.LoadPacks)
    fun updateVideoCallLink(link: String) = actions.trySend(Action.UpdateVideoCallLink(link))
    fun updateName(name: String) = actions.trySend(Action.UpdateName(name))
    fun updateTimeLimit(timeLimit: String) = actions.trySend(Action.UpdateTimeLimit(timeLimit))

    private suspend fun FlowCollector<State>.handleAction(action: Action) {
        when (action) {
            is Action.UpdateVideoCallLink -> handleUpdateVideoCallLink(action.link)
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
        val state = state.value
        if (state.isLoadingCreation) return

        if (state.formState !is FormState.Valid) {
            throwIfDebug { "Create game was called but FormState is not valid" }
            return
        }

        updateState { it.copy(isLoadingCreation = true) }

        if (state.isSingleDevice) {
            createSingleDeviceGame(state)
        } else {
            createMultiDeviceGame(state)
        }.onSuccess {
            _events.trySend(Event.GameCreated(state.isSingleDevice))
        }.onFailure {
            updateState { it.copy(didSomethingGoWrong = true) }
        }
            .eitherWay {
                updateState { it.copy(isLoadingCreation = false) }
            }
    }

    private suspend fun createSingleDeviceGame(state: State): Try<Unit> =
        allOrNone(
            state.timeLimit(),
            state.numberOfPlayers(),
            state.selectedPacks()
        ) { timeLimit, numOfPlayers, selectedPacks ->
            createSingleDeviceGameUseCase(
                timeLimit = timeLimit,
                numOfPlayers = numOfPlayers,
                packs = selectedPacks
            )
        } ?: illegalState(
            """
            Cannot create single device game with
            timeLimit: ${state.timeLimit()},
            numberOfPlayers: ${state.numberOfPlayers()},
            selectedPacks: ${state.selectedPacks()?.map { it.name }},
        """.trimIndent()
        )

    private suspend fun createMultiDeviceGame(state: State): Try<String> = allOrNone(
        state.timeLimit(),
        state.userName(),
        state.selectedPacks()
    ) { timeLimit, userName, selectedPacks ->
        createMultiDeviceGameGameUseCase(
            userName = userName,
            packs = selectedPacks,
            timeLimit = timeLimit,
            videoCallLink = state.videoCallLink.takeIf { it.isNotEmpty() }
        )
    } ?: illegalState(
        """
            Cannot create multi device game with
            timeLimit: ${state.timeLimit()},
            username: ${state.userName()},
            selectedPacks: ${state.selectedPacks()?.map { it.name }},
        """.trimIndent()
    )

    private suspend fun FlowCollector<State>.handleUpdateName(name: String) {
        val nameState = when {
            name.isEmpty() -> Invalid(name, "Name cannot be blank")
            name.length !in gameConfig.minNameLength..gameConfig.maxNameLength -> Invalid(
                name,
                "Name must be between ${gameConfig.minNameLength}-${gameConfig.maxNameLength} characters"
            )

            else -> Valid(name)
        }
        updateState { it.copy(nameState = nameState) }
    }

    private suspend fun FlowCollector<State>.handleUpdateVideoCallLink(link: String) {
        updateState { it.copy(videoCallLink = link) }
    }

    private suspend fun FlowCollector<State>.handleUpdateGameType(isSingleDevice: Boolean) {
        updateState { it.copy(isSingleDevice = isSingleDevice) }
    }

    private suspend fun FlowCollector<State>.handleUpdateNumOfPlayers(numOfPlayers: String) =
        updateState {
            checkInDebug(it.isSingleDevice) {
                "Number of players was updated but game type is not single device"
            }

            // we only allow the user to type 2 digits
            val truncatedNumber = numOfPlayers.take(2)

            val numberOfPlayersState = when {
                truncatedNumber.toIntOrNull() == null -> {
                    Invalid(
                        truncatedNumber,
                        "Invalid Number of players. Please type in a number between ${gameConfig.minPlayers} and ${gameConfig.maxPlayers}."
                    )
                }

                truncatedNumber.toInt() !in gameConfig.minPlayers..gameConfig.maxPlayers -> {
                    Invalid(
                        truncatedNumber,
                        "Games can only have between ${gameConfig.minTimeLimit}-${gameConfig.maxTimeLimit} players."
                    )
                }

                else -> Valid(truncatedNumber)
            }

            it.copy(numberOfPlayersState = numberOfPlayersState)

        }

    private suspend fun FlowCollector<State>.handleUpdateTimeLimit(timeLimit: String) {
        // we only allow the user to type 2 digits
        val truncatedNumber = timeLimit.take(2)

        val timeLimitState = when {
            truncatedNumber.toIntOrNull() == null -> {
                Invalid(
                    truncatedNumber,
                    "Invalid time limit. Please type in a number between ${gameConfig.minTimeLimit} and ${gameConfig.maxTimeLimit}."
                )
            }

            truncatedNumber.toInt() !in gameConfig.minTimeLimit..gameConfig.maxTimeLimit -> {
                Invalid(
                    truncatedNumber,
                    "The game must be between ${gameConfig.minTimeLimit}-${gameConfig.maxTimeLimit} mins."
                )
            }

            else -> Valid(truncatedNumber)
        }

        updateState {
            it.copy(timeLimitState = timeLimitState)
        }
    }

    private suspend fun FlowCollector<State>.handleSelectPack(
        pack: DisplayablePack,
        isSelected: Boolean
    ) {
        updateState { state ->
            val packs = state.packsState.backingValue.orEmpty()
            val updatedPacks = packs.map {
                if (it == pack) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            }
            if (updatedPacks.none { it.isSelected }) {
                state.copy(packsState = Invalid(updatedPacks, "You must select at least one pack"))
            } else {
                state.copy(packsState = Valid(updatedPacks))
            }
        }
    }

    private suspend fun FlowCollector<State>.handleLoadPacks() {
        locationPackRepository.getPacks()
            .map { packs ->
                packs.map { DisplayablePack(it) }
            }.onSuccess { packs ->
                updateState {
                    if (packs.isEmpty()) {
                        it.copy(
                            didSomethingGoWrong = true,
                            packsState = FieldState.Error()
                        )
                    } else {
                        it.copy(packsState = Idle(packs))
                    }
                }
            }.onFailure {
                updateState {
                    it.copy(
                        didSomethingGoWrong = true,
                        packsState = FieldState.Error()
                    )
                }
            }
            .eitherWay { updateState { it.copy(isLoadingPacks = false) } }
    }

    private fun State.withFormValidation(): State {
        val isFormValid = if (isSingleDevice) {
            timeLimitState is Valid
                    && packsState is Valid
                    && numberOfPlayersState is Valid
        } else {
            timeLimitState is Valid &&
                    packsState is Valid
                    && nameState is Valid
        }
        return this.copy(formState = if (isFormValid) FormState.Valid else FormState.Invalid)
    }

    private suspend fun FlowCollector<State>.updateState(update: (State) -> State) {
        Try {
            val currentValue = state.value
            val nextValue = update(currentValue)
            emit(nextValue)
        }
            .logOnError("Could not update state")
            .throwIfDebug()
    }
}
