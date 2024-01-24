package com.dangerfield.features.newgame.internal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.newgame.internal.metrics.NewGameMetricsTracker
import com.dangerfield.features.newgame.internal.presentation.model.Action
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.FieldState
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Idle
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Invalid
import com.dangerfield.features.newgame.internal.presentation.model.FieldState.Valid
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.internal.presentation.model.State
import com.dangerfield.features.newgame.internal.presentation.model.numberOfPlayers
import com.dangerfield.features.newgame.internal.presentation.model.selectedPacks
import com.dangerfield.features.newgame.internal.presentation.model.timeLimit
import com.dangerfield.features.newgame.internal.presentation.model.userName
import com.dangerfield.features.newgame.internal.usecase.CreateGame
import com.dangerfield.features.newgame.internal.usecase.CreateSingleDeviceGame
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.coreflowroutines.launchOnStart
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.oddoneoout.features.newgame.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.Try
import oddoneout.core.allOrNone
import oddoneout.core.checkInDebug
import oddoneout.core.illegalState
import oddoneout.core.logOnError
import oddoneout.core.throwIfDebug
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class NewGameViewModel @Inject constructor(
    private val locationPackRepository: LocationPackRepository,
    private val createMultiDeviceGameGameUseCase: CreateGame,
    private val createSingleDeviceGameUseCase: CreateSingleDeviceGame,
    private val gameConfig: GameConfig,
    private val dictionary: Dictionary,
    private val isRecognizedVideoCallLink: IsRecognizedVideoCallLink,
    private val newGameMetricsTracker: NewGameMetricsTracker
) : ViewModel() {

    // TODO rework this to allow for a debounce on the video call link
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
                videoCallLinkState = Idle(""),
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
    fun resolveSomethingWentWrong() = actions.trySend(Action.ResolveSomethingWentWrong)
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
            Action.ResolveSomethingWentWrong -> handleResolveSomethingWentWrong()
        }
    }

    private suspend fun FlowCollector<State>.handleResolveSomethingWentWrong() {
       updateState { it.copy(didSomethingGoWrong = false) }
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
                .onSuccess { accessCode ->
                    newGameMetricsTracker.trackSingleDeviceGameCreated(
                        packs = state.selectedPacks()?.map { it.name } ?: emptyList(),
                        timeLimit = state.timeLimit() ?: 0,
                        playerCount = state.numberOfPlayers() ?: 0
                    )
                    _events.trySend(Event.SingleDeviceGameCreated(accessCode))
                }
        } else {
            createMultiDeviceGame(state)
                .onSuccess { accessCode ->
                    newGameMetricsTracker.trackMultiDeviceGameCreated(
                        location = state.selectedPacks()?.firstOrNull()?.name ?: "unknown",
                        packs = state.selectedPacks()?.map { it.name } ?: emptyList(),
                        timeLimit = state.timeLimit() ?: 0,
                        videoLink = state.videoCallLinkState.backingValue,
                        accessCode = accessCode
                    )
                    _events.trySend(
                        Event.GameCreated(
                            accessCode,
                            state.videoCallLinkState.takeIf { it.isValid() }?.backingValue
                        )
                    )
                }
        }
            .logOnError()
            .onFailure {
                newGameMetricsTracker.trackErrorCreatingGame(state.isSingleDevice, it)
                updateState { it.copy(didSomethingGoWrong = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoadingCreation = false) }
            }
    }

    private suspend fun createSingleDeviceGame(state: State): Try<String> =
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
            videoCallLink = state.videoCallLinkState.backingValue
        )
        // TODO handle failures dude
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
            name.isEmpty() -> Invalid(name, dictionary.getString(R.string.newGame_blankNameError_text))
            name.length !in gameConfig.minNameLength..gameConfig.maxNameLength -> Invalid(
                name,
                dictionary.getString(
                    R.string.newGame_nameLengthError_text,
                    mapOf(
                        "min" to gameConfig.minNameLength.toString(),
                        "max" to gameConfig.maxNameLength.toString()
                    )
                )
            )

            else -> Valid(name)
        }
        updateState { it.copy(nameState = nameState) }
    }

    private suspend fun FlowCollector<State>.handleUpdateVideoCallLink(link: String) {
        updateState {
            val videoCallLinkState = if (link.isEmpty()) {
                Idle(link)
            } else if (isRecognizedVideoCallLink(link)) {
                Valid(link)
            } else {
                Invalid(
                    link,
                    dictionary.getString(R.string.newGame_invalidLinkError_text)
                )
            }
            it.copy(videoCallLinkState = videoCallLinkState)
        }
    }

    private suspend fun FlowCollector<State>.handleUpdateGameType(isSingleDevice: Boolean) {
        checkInDebug(gameConfig.isSingleDeviceModeEnabled) {
            "Update game type was called but single device mode is not enabled"
        }
        updateState { it.copy(isSingleDevice = isSingleDevice) }
    }

    private suspend fun FlowCollector<State>.handleUpdateNumOfPlayers(numOfPlayers: String) =
        updateState {
            checkInDebug(it.isSingleDevice) {
                "Number of players was updated but game type is not single device"
            }

            // we only allow the user to type 2 digits
            val truncatedNumber = numOfPlayers.take(2)

            val numberOfPlayersState = if (truncatedNumber.toIntOrNull() == null ||
                truncatedNumber.toIntOrNull() !in gameConfig.minPlayers..gameConfig.maxPlayers
            ) {
                Invalid(
                    truncatedNumber,
                    dictionary.getString(
                        R.string.newGame_invalidNumOfPlayersError_text,
                        mapOf(
                            "min" to gameConfig.minPlayers.toString(),
                            "max" to gameConfig.maxPlayers.toString()
                        )
                    )
                )
            }
            else Valid(truncatedNumber)

            it.copy(numberOfPlayersState = numberOfPlayersState)

        }

    private suspend fun FlowCollector<State>.handleUpdateTimeLimit(timeLimit: String) {
        // we only allow the user to type 2 digits
        val truncatedNumber = timeLimit.take(2)

        val timeLimitState = if (truncatedNumber.toIntOrNull() !in gameConfig.minTimeLimit..gameConfig.maxTimeLimit ||
            truncatedNumber.toIntOrNull() == null
        ) {
            Invalid(
                truncatedNumber,
                dictionary.getString(
                    R.string.newGame_invalidTimeLimit_text,
                    mapOf(
                        "min" to gameConfig.minTimeLimit.toString(),
                        "max" to gameConfig.maxTimeLimit.toString()
                    )
                )
            )
        }
        else Valid(truncatedNumber)

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
                state.copy(packsState = Invalid(updatedPacks,
                    dictionary.getString(R.string.newGame_noPacksSelectedError_text)))
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
                    && (videoCallLinkState is Valid || videoCallLinkState is Idle)
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
