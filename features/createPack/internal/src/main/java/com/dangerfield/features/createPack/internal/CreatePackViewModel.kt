package com.dangerfield.features.createPack.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.features.createPack.internal.CreatePackViewModel.Action.Load
import com.dangerfield.features.newgame.NewGamePrefs
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.coreflowroutines.collectIn
import com.dangerfield.libraries.dictionary.GetAppLanguageCode
import com.dangerfield.libraries.game.GetNewCustomPackId
import com.dangerfield.libraries.game.OwnerDetails
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.game.PackType
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.FieldState.Idle
import com.dangerfield.libraries.ui.FieldState.Invalid
import com.dangerfield.libraries.ui.FieldState.Valid
import com.dangerfield.libraries.ui.showDeveloperMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import oddoneout.core.debugSnackOnError
import oddoneout.core.logOnFailure
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CreatePackViewModel @Inject constructor(
    private val maxCustomPackItemCount: MaxCustomPackItemCount,
    private val packRepository: PackRepository,
    private val getNewCustomPackId: GetNewCustomPackId,
    private val getAppLanguageCode: GetAppLanguageCode,
    private val newGamePrefs: NewGamePrefs,
    savedStateHandle: SavedStateHandle
) : SEAViewModel<CreatePackViewModel.State, CreatePackViewModel.Event, CreatePackViewModel.Action>(
    savedStateHandle = savedStateHandle,
    initialStateArg = State(packId = getNewCustomPackId())
) {

    private val packInProgress = MutableStateFlow(
        Pack.CustomPack(
            name = "",
            id = state.packId,
            version = 0,
            languageCode = getAppLanguageCode(),
            isPublic = true,
            owner = OwnerDetails.MeUser,
            isUserSaved = false,
            items = listOf(),
            hasUserPlayed = false
        )
    )

    private val existingCustomPacks: StateFlow<List<Pack.CustomPack>>? = packRepository
        .getUsersSavedPacksFlow()
        .getOrNull()
        ?.map { it.filterIsInstance<Pack.CustomPack>() }
        ?.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        takeAction(Load)
    }

    override suspend fun handleAction(action: Action) {
        with(action) {
            when (this) {
                is Load -> handleLoad()
                is Action.UpdateIsPublic -> handlePublicUpdated()
                is Action.UpdateName -> handleUpdateName()
                is Action.SavePack -> handleSavePack()
            }
        }
    }

    private suspend fun Action.SavePack.handleSavePack() {

        packRepository.updateCachedPackDetails(
            id = packInProgress.value.packId,
            isUserSaved = true,
            isPendingSave = false
        )
            .logOnFailure()
            .onSuccess {
                sendEvent(Event.PackCreated)
            }
            .onFailure {
                showDeveloperMessage { "Could not save pack" }
            }
    }

    private suspend fun Action.UpdateName.handleUpdateName() {
        // immediately update state but dont update the pack in progress
        updateState { it.copy(nameFieldState = Idle(name)) }

        when {
            name.isBlank() -> updateState {
                it.copy(nameFieldState = Invalid(name, "Name cannot be blank"))
            }
            name.length < 3 -> updateState {
                it.copy(
                    nameFieldState = Invalid(name, "Name must be at least 3 characters")
                )
            }
            existingCustomPacks?.value?.any { it.name == name } == true -> updateState {
                it.copy(
                    nameFieldState = Invalid(name, "It looks like you already have a pack with this name")
                )
            }
            else -> {
               packInProgress.update {
                   it.copy(name = name)
               }
            }
        }
    }

    private suspend fun Action.UpdateIsPublic.handlePublicUpdated() {
        packInProgress.update {
            it.copy(isPublic = isPublic)
        }
    }

    private suspend fun Load.handleLoad() {
        newGamePrefs.hasUsedCreateYourOwnPack = true

        observePackInProgressForCacheUpdates()

        observePackItemUpdates()

        observePackInProgressForStateUpdates()
    }

    private fun Load.observePackInProgressForStateUpdates() {
        packInProgress
            .drop(1) // ignore initial state value
            .collectIn(viewModelScope) { updatedPack ->
            updateState {
                it.copy(
                    // We validate before saving
                    nameFieldState = Valid(updatedPack.name),
                    isPublic = updatedPack.isPublic,
                    items = updatedPack.items,
                    canAddMoreItems = updatedPack.items.size < maxCustomPackItemCount()
                )
            }
        }
    }

    private fun observePackItemUpdates() {
        packRepository
            .getCachedPackFlow(state.packId)
            .getOrNull()
            ?.filterIsInstance(Pack.CustomPack::class)
            ?.collectIn(viewModelScope) { cachedPack ->
                val customPackItems = cachedPack.packItems as? List<PackItem.Custom>
                packInProgress.update {
                    it.copy(
                        items = customPackItems ?: emptyList(),
                    )
                }
            } ?: sendEvent(Event.CouldNotLoadPack)
    }

    private fun observePackInProgressForCacheUpdates() {
        packInProgress
            .drop(1) // dont count initial state from state flow
            // to avoid infinite loop, ignore changes to the pack items
            .distinctUntilChanged { old, new ->
                old.id == new.id
                        && old.name == new.name
                        && old.version == new.version
                        && old.languageCode == new.languageCode
                        && old.isPublic == new.isPublic
                        && old.owner == new.owner
                        && old.isUserSaved == new.isUserSaved
                        && old.packType == new.packType
            }
            .collectIn(viewModelScope) { pack ->
                packRepository.updateCachedPackDetails(
                    id = pack.packId,
                    name = pack.packName,
                    version = pack.packVersion,
                    languageCode = pack.packLanguageCode,
                    isPublic = pack.packIsPublic,
                    owner = pack.packOwner,
                    isUserSaved = pack.packIsUserSaved,
                    packType = PackType.Custom,
                    isPendingSave = true
                ).debugSnackOnError { "Could not save pack in progress" }
            }
    }

    sealed class Action {
        data object Load : Action()
        data class UpdateName(val name: String) : Action()
        data class UpdateIsPublic(val isPublic: Boolean) : Action()
        data object SavePack : Action()
    }

    sealed class Event {
        data object PackCreated : Event()
        data object CouldNotLoadPack : Event()
    }

    data class State(
        val nameFieldState: FieldState<String> = Idle(""),
        val isPublic: Boolean = true,
        val items: List<PackItem.Custom> = emptyList(),
        val canAddMoreItems: Boolean = true,
        val packId: String
    )
}

