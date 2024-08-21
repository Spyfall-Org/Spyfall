package com.dangerfield.features.createPack.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dangerfield.libraries.coreflowroutines.SEAViewModel
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.game.PackRepository
import com.dangerfield.libraries.navigation.navArgument
import com.dangerfield.libraries.ui.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import oddoneout.core.eitherWay
import javax.inject.Inject

@HiltViewModel
class EditPackItemViewModel @Inject constructor(
    private val maxCustomPackItemCount: MaxCustomPackItemCount,
    private val packRepository: PackRepository,
    private val savedStateHandle: SavedStateHandle
) : SEAViewModel<EditPackItemViewModel.State, EditPackItemViewModel.Event, EditPackItemViewModel.Action>(
    savedStateHandle = savedStateHandle,
    initialStateArg = State(maximumItems = maxCustomPackItemCount.value)

) {
    private val packId: String
        get() = savedStateHandle.navArgument(packIdArgument) ?: ""

    private val packItemName: String?
        get() = savedStateHandle.navArgument(packItemNameArgument)

    init {
        takeAction(Action.Load)
    }

    private val existingPackStateFlow: StateFlow<Pack.CustomPack?>? =
        packRepository
            .getUsersSavedPacksFlow()
            .getOrNull()
            ?.map {
                it.filterIsInstance<Pack.CustomPack>().firstOrNull { it.id == packId }
            }
            ?.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    override suspend fun handleAction(action: Action) {
        with(action) {
            when (this) {
                is Action.Load -> handleLoad()

                is Action.UpdateName -> handleUpdateName()

                is Action.AddItemToPack -> handleAddItemToPack()

                is Action.AddRole -> handleAddRole()

                is Action.UpdateRoleName -> handleUpdateRoleName()

                is Action.RemoveRole -> handleRemoveRole()
            }
        }
    }

    private suspend fun Action.RemoveRole.handleRemoveRole() {
        updateState {
            it.copy(roleFields = it.roleFields.filterIndexed { index, _ -> index != this.index })
        }
    }

    private suspend fun Action.AddRole.handleAddRole() {
        updateState {
            it.copy(roleFields = it.roleFields + FieldState.Idle(""))
        }
    }

    private suspend fun Action.UpdateRoleName.handleUpdateRoleName() {
        updateState {
            it.copy(roleFields = it.roleFields.mapIndexed { index, fieldState ->
                if (index == this.index) {
                    when {
                        name.isBlank() -> FieldState.Invalid(name, "Role cannot be blank")
                        name.length < 2 -> FieldState.Invalid(
                            name, "Role must be at least 2 characters"
                        )

                        else -> FieldState.Valid(name)
                    }
                } else {
                    fieldState
                }
            })
        }
    }

    private suspend fun Action.AddItemToPack.handleAddItemToPack() {
        updateState { it.copy(isLoadingAdd = true) }

        val name = state.nameFieldState.value
        val roles = state.roleFields.mapNotNull { it.value }

        if (name.isNullOrBlank() || roles.any { it.isBlank() }) {
            sendEvent(Event.CouldNotAddItem)
            return
        }

        val packItem = PackItem.Custom(
            name = name,
            roles = roles,
        )

        packRepository.addPackItem(packId = packId, item = packItem).onFailure {
            sendEvent(Event.CouldNotAddItem)
        }.onSuccess {
            sendEvent(Event.ItemAdded)
        }.eitherWay {
            updateState { it.copy(isLoadingAdd = false) }
        }
    }

    private suspend fun Action.UpdateName.handleUpdateName() {
        when {
            name.isBlank() -> updateState {
                it.copy(nameFieldState = FieldState.Invalid(name, "Name cannot be blank"))
            }

            name.length < 3 -> updateState {
                it.copy(
                    nameFieldState = FieldState.Invalid(
                        name, "Name must be at least 3 characters"
                    )
                )
            }

            existingPackStateFlow?.value?.items?.any { it.name == name } == true -> updateState {
                it.copy(nameFieldState = FieldState.Invalid(name, "Name already exists"))
            }

            else -> {
                updateState {
                    it.copy(nameFieldState = FieldState.Valid(name))
                }
            }
        }
    }

    private suspend fun Action.Load.handleLoad() {
        if (packItemName != null) {

            updateState { it.copy(isLoadingItems = true) }

            val pack = packRepository.getCachedPackFlow(packId).getOrNull()?.first()

            if (pack == null) {
                sendEvent(Event.CouldNotLoadPack)
            } else {
                pack.packItems.firstOrNull { it.name == packItemName }?.let { item ->
                    updateState {
                        it.copy(nameFieldState = FieldState.Valid(item.name),
                            roleFields = item.roles?.map { role -> FieldState.Valid(role) }
                                ?: emptyList())
                    }
                }
            }

            updateState { it.copy(isLoadingItems = false) }
        }
    }

    override suspend fun mapEachState(state: State): State {
        return state.copy(
            isFormValid = state.nameFieldState is FieldState.Valid
                    && state.roleFields.all { it is FieldState.Valid }
        )
    }

    sealed class Action {
        data object Load : Action()
        data object AddItemToPack : Action()
        data class UpdateRoleName(val index: Int, val name: String) : Action()
        data object AddRole : Action()
        data class UpdateName(val name: String) : Action()
        data class RemoveRole(val index: Int) : Action()
    }

    sealed class Event {
        data object ItemAdded : Event()
        data object CouldNotLoadPack : Event()
        data object CouldNotAddItem : Event()
    }

    data class State(
        val nameFieldState: FieldState<String> = FieldState.Idle(""),
        val isLoadingAdd: Boolean = false,
        val isLoadingItems: Boolean = false,
        val roleFields: List<FieldState<String>> = emptyList(),
        val maximumItems: Int,
        val isFormValid: Boolean = false,
    )
}

