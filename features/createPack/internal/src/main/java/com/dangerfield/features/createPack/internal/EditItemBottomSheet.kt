package com.dangerfield.features.createPack.internal

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dangerfield.libraries.analytics.PageLogEffect
import com.dangerfield.libraries.analytics.PageType
import com.dangerfield.libraries.navigation.route
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.HorizontalSpacerD200
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.VerticalSpacerD1000
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BasicBottomSheet
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetState
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.BottomSheetValue
import com.dangerfield.libraries.ui.components.dialog.bottomsheet.rememberBottomSheetState
import com.dangerfield.libraries.ui.components.icon.CircleIcon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.IconSize
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.AsteriskText
import com.dangerfield.libraries.ui.components.text.InputField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@Composable
fun EditItemBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(),
    nameFieldState: FieldState<String>,
    onNameChanged: (String) -> Unit,
    roleFields: List<FieldState<String>>,
    onRoleChanged: (Int, String) -> Unit,
    onDeleteRole: (Int) -> Unit,
    isAddingNewItem: Boolean,
    onAddRoleField: () -> Unit,
    isFormValid: Boolean,
    onDismiss: (BottomSheetState) -> Unit,
    onDone: () -> Unit,
) {
    PageLogEffect(
        route = route("create_your_own_pack_add_item"), type = PageType.BottomSheet
    )

    BasicBottomSheet(onDismissRequest = { onDismiss(bottomSheetState) },
        state = bottomSheetState,
        modifier = modifier,
        stickyTopContent = {
            if (isAddingNewItem) {
                Text(text = "Add item to your pack")
            }
        },
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsteriskText(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Name",
                        typography = OddOneOutTheme.typography.Heading.H800,
                    )
                }
                VerticalSpacerD500()

                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    title = null,
                    fieldState = nameFieldState,
                    onFieldUpdated = onNameChanged,
                    hint = "Enter a name for your pack item"
                )

                VerticalSpacerD1000()

                Text(
                    text = "Roles (Optional)",
                    typography = OddOneOutTheme.typography.Heading.H800,
                    modifier = Modifier.fillMaxWidth()
                )

                VerticalSpacerD500()

                roleFields.forEachIndexed { index, fieldState ->
                    Row(
                        modifier =
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InputField(
                            modifier = Modifier.weight(1f),
                            title = null,
                            fieldState = fieldState,
                            onFieldUpdated = { onRoleChanged(index, it) },
                            hint = "Enter a role"
                        )

                        HorizontalSpacerD200()

                        IconButton(
                            icon = SpyfallIcon.Close("Remove"),
                            onClick = { onDeleteRole(index) },
                        )
                    }
                }

                VerticalSpacerD800()

                CircleIcon(
                    modifier = Modifier.bounceClick(onClick = onAddRoleField),
                    icon = SpyfallIcon.Add("Add"),
                    iconSize = IconSize.Large,
                    padding = Dimension.D100
                )

                VerticalSpacerD500()
            }
        },
        stickyBottomContent = {
            Button(
                style = ButtonStyle.Background,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
                onClick = onDone,
                type = ButtonType.Primary,
            ) {
                Text(text = "Done")
            }
        })
}

@Composable
@Preview
private fun PreviewAddItemBottomSheet() {
    val bottomSheetState = rememberBottomSheetState(initialState = BottomSheetValue.Expanded)

    Preview {
        EditItemBottomSheet(
            onDismiss = {},
            bottomSheetState = bottomSheetState,
            nameFieldState = FieldState.Idle(""),
            onNameChanged = { },
            roleFields = listOf(FieldState.Idle("")),
            onRoleChanged = { _, _ -> },
            isFormValid = false,
            onAddRoleField = { -> },
            onDone = { -> },
            isAddingNewItem = true,
            onDeleteRole = {}
        )
    }
}
