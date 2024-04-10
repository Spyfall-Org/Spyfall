package com.dangerfield.features.newgame.internal.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.newgame.internal.metrics.NewGameMetricsTracker
import com.dangerfield.features.newgame.internal.presentation.model.Action
import com.dangerfield.features.newgame.internal.presentation.model.DisplayablePack
import com.dangerfield.features.newgame.internal.presentation.model.Event
import com.dangerfield.features.newgame.internal.presentation.model.FormState
import com.dangerfield.features.newgame.internal.presentation.model.State
import com.dangerfield.features.newgame.internal.presentation.model.numberOfPlayers
import com.dangerfield.features.newgame.internal.presentation.model.selectedPacks
import com.dangerfield.features.newgame.internal.presentation.model.timeLimit
import com.dangerfield.features.newgame.internal.presentation.model.userName
import com.dangerfield.features.newgame.internal.usecase.CreateGame
import com.dangerfield.features.newgame.internal.usecase.CreateSingleDeviceGame
import com.dangerfield.features.videoCall.IsRecognizedVideoCallLink
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.collectInWithPrevious
import com.dangerfield.libraries.dictionary.Dictionary
import com.dangerfield.libraries.dictionary.getString
import com.dangerfield.libraries.game.GameConfig
import com.dangerfield.libraries.game.LocationPackRepository
import com.dangerfield.libraries.game.LocationPacksResult.Hit
import com.dangerfield.libraries.game.LocationPacksResult.Miss
import com.dangerfield.libraries.network.NetworkMonitor
import com.dangerfield.libraries.session.UserRepository
import com.dangerfield.libraries.ui.FieldState.Idle
import com.dangerfield.libraries.ui.FieldState.Invalid
import com.dangerfield.libraries.ui.FieldState.Valid
import com.dangerfield.libraries.ui.isValid
import com.dangerfield.oddoneoout.features.newgame.internal.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import oddoneout.core.Catching
import oddoneout.core.allOrNone
import oddoneout.core.checkInDebug
import oddoneout.core.eitherWay
import oddoneout.core.illegalStateFailure
import oddoneout.core.logOnFailure
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
    private val userRepository: UserRepository,
    private val isRecognizedVideoCallLink: IsRecognizedVideoCallLink,
    private val newGameMetricsTracker: NewGameMetricsTracker,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle
) : SEAViewModel<State, Event, Action>(savedStateHandle) {

    init {
        takeAction(Action.Init)
    }

    override fun initialState() = State(
        packsState = Idle(emptyList()),
        timeLimitState = Valid("8"),
        nameState = Idle(""),
        videoCallLinkState = Idle(""),
        isLoadingPacks = true,
        isLoadingCreation = false,
        didLoadFail = false,
        didCreationFail = false,
        formState = FormState.Idle,
        isSingleDevice = false,
        numberOfPlayersState = Idle(""),
        isOffline = false
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            is Action.LoadPacks -> action.loadPacks()
            is Action.Init -> action.handleInit()
            is Action.UpdateVideoCallLink -> action.handleUpdateVideoCallLink()
            is Action.CreateGame -> action.handleCreateGame()
            is Action.UpdateName -> action.handleUpdateName()
            is Action.UpdateTimeLimit -> action.handleUpdateTimeLimit()
            is Action.UpdateGameType -> action.handleUpdateGameType()
            is Action.UpdateNumOfPlayers -> action.handleUpdateNumOfPlayers()
            is Action.SelectPack -> action.handleSelectPack()
            is Action.ResolveErrors -> action.handleResolveErrors()
        }
    }

    fun updateGameType(isSingleDevice: Boolean) =
        takeAction(Action.UpdateGameType(isSingleDevice))

    fun updateNumOfPlayers(numOfPlayers: String) =
        takeAction(Action.UpdateNumOfPlayers(numOfPlayers))

    fun selectPack(pack: DisplayablePack, isSelected: Boolean) =
        takeAction(Action.SelectPack(pack, isSelected))

    fun createGame() = takeAction(Action.CreateGame)
    fun updateVideoCallLink(link: String) = takeAction(Action.UpdateVideoCallLink(link))
    fun resolveSomethingWentWrong() = takeAction(Action.ResolveErrors)
    fun updateName(name: String) = takeAction(Action.UpdateName(name))
    fun updateTimeLimit(timeLimit: String) = takeAction(Action.UpdateTimeLimit(timeLimit))

    private suspend fun Action.ResolveErrors.handleResolveErrors() {
        updateState {
            it.copy(
                didLoadFail = false,
                didCreationFail = false
            )
        }
    }

    private suspend fun Action.CreateGame.handleCreateGame() {
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
                    sendEvent(Event.SingleDeviceGameCreated(accessCode))
                }
        } else {
            createMultiDeviceGame(state)
                .onSuccess { accessCode ->
                    newGameMetricsTracker.trackMultiDeviceGameCreated(
                        location = state.selectedPacks()?.firstOrNull()?.name ?: "unknown",
                        packs = state.selectedPacks()?.map { it.name } ?: emptyList(),
                        timeLimit = state.timeLimit() ?: 0,
                        videoLink = state.videoCallLinkState.value,
                        accessCode = accessCode
                    )
                    sendEvent(
                        Event.GameCreated(
                            accessCode,
                            state.videoCallLinkState.takeIf { it.isValid() }?.value
                        )
                    )
                }
        }
            .logOnFailure()
            .onFailure {
                newGameMetricsTracker.trackErrorCreatingGame(state.isSingleDevice, it)
                updateState { it.copy(didCreationFail = true) }
            }
            .eitherWay {
                updateState { it.copy(isLoadingCreation = false) }
            }
    }

    private suspend fun createSingleDeviceGame(state: State): Catching<String> =
        allOrNone(
            state.timeLimit(),
            state.numberOfPlayers(),
            state.selectedPacks()
        ) { timeLimit, numOfPlayers, selectedPacks ->
            createSingleDeviceGameUseCase(
                timeLimit = timeLimit,
                numOfPlayers = numOfPlayers,
                locationPacks = selectedPacks
            )
        } ?: illegalStateFailure {
            """
            Cannot create single device game with
            timeLimit: ${state.timeLimit()},
            numberOfPlayers: ${state.numberOfPlayers()},
            selectedPacks: ${state.selectedPacks()?.map { it.name }},
        """.trimIndent()
        }

    private suspend fun createMultiDeviceGame(state: State): Catching<String> = allOrNone(
        state.timeLimit(),
        state.userName(),
        state.selectedPacks()
    ) { timeLimit, userName, selectedPacks ->
        createMultiDeviceGameGameUseCase(
            userName = userName,
            locationPacks = selectedPacks,
            timeLimit = timeLimit,
            videoCallLink = state.videoCallLinkState.value
        )
        // TODO handle failures dude
    } ?: illegalStateFailure {
        """
            Cannot create multi device game with
            timeLimit: ${state.timeLimit()},
            username: ${state.userName()},
            selectedPacks: ${state.selectedPacks()?.map { it.name }},
        """.trimIndent()
    }

    private suspend fun Action.UpdateName.handleUpdateName() {
        val nameState = when {
            name.isEmpty() -> Invalid(
                name,
                dictionary.getString(R.string.newGame_blankNameError_text)
            )

            name.length !in gameConfig.minNameLength..gameConfig.maxNameLength -> Invalid(
                name,
                dictionary.getString(
                    R.string.newGame_nameLengthError_text,
                    "min" to gameConfig.minNameLength.toString(),
                    "max" to gameConfig.maxNameLength.toString()
                )
            )

            else -> Valid(name)
        }
        updateState { it.copy(nameState = nameState) }
    }

    private suspend fun Action.UpdateVideoCallLink.handleUpdateVideoCallLink() {
        updateState {
            it.copy(videoCallLinkState = Idle(link))
        }

        updateStateDebounced {
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

    private suspend fun Action.UpdateGameType.handleUpdateGameType() {
        checkInDebug(gameConfig.isSingleDeviceModeEnabled) {
            "Update game type was called but single device mode is not enabled"
        }
        updateState { it.copy(isSingleDevice = isSingleDevice || it.isOffline) }
    }

    private suspend fun Action.UpdateNumOfPlayers.handleUpdateNumOfPlayers() =
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
            } else Valid(truncatedNumber)

            it.copy(numberOfPlayersState = numberOfPlayersState)

        }

    private suspend fun Action.UpdateTimeLimit.handleUpdateTimeLimit() {
        // we only allow the user to type 2 digits
        val truncatedNumber = timeLimit.take(2)

        val timeLimitState =
            if (truncatedNumber.toIntOrNull() !in gameConfig.minTimeLimit..gameConfig.maxTimeLimit ||
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
            } else Valid(truncatedNumber)

        updateState {
            it.copy(timeLimitState = timeLimitState)
        }
    }

    private suspend fun Action.SelectPack.handleSelectPack() {
        updateState { state ->
            val packs = state.packsState.value.orEmpty()
            val updatedPacks = packs.map {
                if (it == pack) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            }
            if (updatedPacks.none { it.isSelected }) {
                state.copy(
                    packsState = Invalid(
                        updatedPacks,
                        dictionary.getString(R.string.newGame_noPacksSelectedError_text)
                    )
                )
            } else {
                state.copy(packsState = Valid(updatedPacks))
            }
        }
    }

    private suspend fun Action.Init.handleInit() {
        takeAction(Action.LoadPacks)

        networkMonitor.isOnline.collectInWithPrevious(viewModelScope) { wasOnline, isOnline ->
            updateState {
                it.copy(
                    isOffline = !isOnline,
                    isSingleDevice = it.isSingleDevice || !isOnline
                )
            }
            val cameBackOnline = wasOnline == false && isOnline
            if (cameBackOnline) {
                takeAction(Action.LoadPacks)
            }
        }
    }

    private suspend fun Action.LoadPacks.loadPacks() {
        updateState { it.copy(didLoadFail = false) }
        locationPackRepository.getPacks(
            languageCode = userRepository.getUserFlow().first().languageCode,
            version = gameConfig.packsVersion
        )
            .logOnFailure()
            .map { result ->
                when (result) {
                    is Hit -> result.packs
                    is Miss -> result.packs  // ignore misses, if the user is offline we will limit their experience
                }.map { DisplayablePack(it) }
            }.onSuccess { packs ->
                updateState {
                    if (packs.isEmpty()) {
                        it.copy(didLoadFail = true)
                    } else {
                        it.copy(packsState = Idle(packs))
                    }
                }
            }.onFailure {
                updateState { it.copy(didLoadFail = true) }
            }
            .eitherWay { updateState { it.copy(isLoadingPacks = false) } }
    }

    override suspend fun mapEachState(state: State): State {
        val isFormValid = if (state.isSingleDevice) {
            state.timeLimitState is Valid
                    && state.packsState is Valid
                    && state.numberOfPlayersState is Valid
        } else {
            state.timeLimitState is Valid &&
                    state.packsState is Valid
                    && state.nameState is Valid
                    && (state.videoCallLinkState is Valid || state.videoCallLinkState is Idle)
        }

        return state.copy(
            formState = if (isFormValid) FormState.Valid else FormState.Invalid,
            isSingleDevice = state.isSingleDevice || state.isOffline
        )
    }
}
