package com.dangerfield.features.gameplay.internal.singledevice.voting

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.gameplay.accessCodeArgument
import com.dangerfield.features.gameplay.internal.DisplayablePlayer
import com.dangerfield.features.gameplay.internal.singledevice.voting.SingleDeviceVotingViewModel.*
import com.dangerfield.features.gameplay.internal.toDisplayable
import com.dangerfield.libraries.coreflowroutines.LazySuspend
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.GameRepository
import com.dangerfield.libraries.game.GameResult
import com.dangerfield.libraries.game.GameState
import com.dangerfield.libraries.game.MapToGameStateUseCase
import com.dangerfield.libraries.game.SingleDeviceRepositoryName
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.session.ClearActiveGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import spyfallx.core.developerSnackIfDebug
import spyfallx.core.doNothing
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SingleDeviceVotingViewModel @Inject constructor(
    @Named(SingleDeviceRepositoryName) private val gameRepository: GameRepository,
    private val savedStateHandle: SavedStateHandle,
    private val mapToGameState: MapToGameStateUseCase,
    private val clearActiveGame: ClearActiveGame,
) : SEAViewModel<State, Event, Action>() {

    private val playersToShowRoles: MutableSet<DisplayablePlayer> = LinkedHashSet()

    private val gameFlow by lazy {
        gameRepository.getGameFlow(accessCode)
    }

    private val allPlayers = LazySuspend {
        gameFlow.first().players.mapIndexed { index, player ->
            player.toDisplayable(index == 0)
        }
    }

    private val accessCode: String
        get() = savedStateHandle.navArgument(accessCodeArgument) ?: ""

    private val hasLoadedGame = AtomicBoolean(false)

    override val initialState = State(
        currentPlayer = null,
        isLastPlayer = false,
        locationOptions = emptyList(),
        playerOptions = emptyList(),
        isLoadingResult = false,
        location = "",
        oddOneOutName = "",
    )

    override suspend fun handleAction(action: Action) {
        when (action) {
            Action.LoadGame -> loadGame()
            Action.EndGame -> endGame()
            Action.ResetGame -> resetGame()
            is Action.SubmitVoteForLocation -> submitVoteForLocation(action)
            is Action.SubmitVoteForPlayer -> submitVoteForPlayer(action)
            Action.WaitForResults -> updateState { state -> state.copy(isLoadingResult = true)  }
        }
    }

    private suspend fun submitVoteForLocation(action: Action.SubmitVoteForLocation) {
        pollPlayer()
        gameRepository.submitLocationVote(
            accessCode = accessCode,
            voterId = action.voterId,
            location = action.location,
        )
    }

    private suspend fun submitVoteForPlayer(action: Action.SubmitVoteForPlayer) {
        pollPlayer()
        gameRepository.submitOddOneOutVote(
            accessCode = accessCode,
            voterId = action.voterId,
            voteId = action.playerId
        )
    }

    private fun loadGame() {
        if (hasLoadedGame.getAndSet(true)) return
        setInitialGameState()
        listenForGameUpdates()
    }

    private fun setInitialGameState() {
        viewModelScope.launch {
            gameRepository.getGame(accessCode)
                .onSuccess { game ->
                    val displayablePlayers = game.players.mapIndexed { index, player ->
                        player.toDisplayable(index == 0)
                    }

                    val playersToVote = game.players.filter { !it.hasVoted() }

                    updateState { state ->
                        state.copy(
                            locationOptions = game.locationOptionNames,
                            playerOptions = displayablePlayers,
                            location = game.locationName,
                            oddOneOutName = displayablePlayers.first { it.isOddOneOut }.name,
                        )
                    }

                    playersToShowRoles.clear()
                    playersToShowRoles.addAll(playersToVote.map { it.toDisplayable(false) })
                    pollPlayer()
                }
        }
    }

    private fun listenForGameUpdates() {
        viewModelScope.launch {
            gameFlow
                .map { game ->
                    mapToGameState(accessCode, game)
                }.collect { gameState ->
                    when (gameState) {
                        is GameState.Expired,
                        is GameState.Started,
                        is GameState.Starting,
                        is GameState.Unknown -> developerSnackIfDebug {
                            "Illegal game state ${gameState::class.java.simpleName}"
                        }

                        is GameState.Waiting -> {
                            sendEvent(Event.GameReset)
                        }

                        is GameState.DoesNotExist -> {
                            clearActiveGame()
                            sendEvent(Event.GameKilled)
                        }

                        is GameState.Voting -> doNothing()
                        is GameState.VotingEnded -> {
                            Log.d("Elijah", "Got vote ending state: $gameState")
                            updateState { state ->
                                state.copy(
                                    location = gameState.location,
                                    oddOneOutName = gameState.players.first { it.isOddOneOut }.userName,
                                    result = gameState.result,
                                    isLoadingResult = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun pollPlayer() {
        val allPlayers = allPlayers.getValue()
        val nextPlayer = playersToShowRoles.elementAtOrNull(0)

        val currentPlayer = nextPlayer?.let {
            playersToShowRoles.remove(it)
            it
        }

        if (currentPlayer != null) {
            updateState { state ->
                state.copy(
                    currentPlayer = currentPlayer,
                    isLastPlayer = playersToShowRoles.isEmpty(),
                    playerOptions = allPlayers.filter { it.id != currentPlayer.id }
                )
            }
        }
    }

    private suspend fun endGame() {
        gameRepository.end(accessCode)
        // TODO pack game ending behind a uses case and leave game for that matter.
        // not super sure I need a data source as well as a repository
        // maybe I continue trucking on and clean that up later

        clearActiveGame()
        sendEvent(Event.GameKilled)
    }

    private suspend fun resetGame() {
        gameRepository.reset(accessCode)
    }

    data class State(
        val currentPlayer: DisplayablePlayer?,
        val isLastPlayer: Boolean,
        val locationOptions: List<String>,
        val isLoadingResult: Boolean,
        val playerOptions: List<DisplayablePlayer>,
        val location: String,
        val oddOneOutName: String,
        val result: GameResult? = null,
    )

    sealed class Event {
        data object GameKilled : Event()
        data object GameReset : Event()
    }

    sealed class Action {
        data object LoadGame : Action()
        data class SubmitVoteForPlayer(val voterId: String, val playerId: String) : Action()
        data object WaitForResults : Action()
        data class SubmitVoteForLocation(val voterId: String, val location: String) : Action()
        data object EndGame : Action()
        data object ResetGame : Action()
    }
}