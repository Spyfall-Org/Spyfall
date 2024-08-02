package com.dangerfield.features.createPack.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.game.Pack
import com.dangerfield.libraries.game.PackItem
import com.dangerfield.libraries.ui.Dimension
import com.dangerfield.libraries.ui.FieldState
import com.dangerfield.libraries.ui.HorizontalSpacerD400
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.Radii
import com.dangerfield.libraries.ui.VerticalSpacerD1200
import com.dangerfield.libraries.ui.VerticalSpacerD500
import com.dangerfield.libraries.ui.VerticalSpacerD800
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.ErrorBehavior
import com.dangerfield.libraries.ui.components.Screen
import com.dangerfield.libraries.ui.components.button.Button
import com.dangerfield.libraries.ui.components.button.ButtonStyle
import com.dangerfield.libraries.ui.components.button.ButtonType
import com.dangerfield.libraries.ui.components.header.Header
import com.dangerfield.libraries.ui.components.icon.Icon
import com.dangerfield.libraries.ui.components.icon.IconButton
import com.dangerfield.libraries.ui.components.icon.SpyfallIcon
import com.dangerfield.libraries.ui.components.text.InputField
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.isValid
import com.dangerfield.libraries.ui.theme.OddOneOutTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddItemsScreen(
    modifier: Modifier = Modifier,
    name: String,
    items: List<PackItem.Custom>,
    maximumItems: Int,
    onAddItemClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val (nameFocusRequester) = remember { FocusRequester.createRefs() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    Screen(
        modifier = modifier,
        topBar = {
            Header(
                navigationIcon = SpyfallIcon.ArrowBack("Close"),
                onNavigateBack = onNavigateBack,
                title = "Step 2 of 3",
                titleAlignment = Alignment.CenterHorizontally,
                typographyToken = OddOneOutTheme.typography.Label.L800
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(it)
                .padding(horizontal = Dimension.D1000),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VerticalSpacerD800()

            Text(text = name)

            VerticalSpacerD1200()

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Add Items To Your Pack",
                typography = OddOneOutTheme.typography.Label.L600
            )

            VerticalSpacerD500()

            ItemsList(
                items,
                onEditItem = {}
            )

            VerticalSpacerD1200()

            if (items.size < maximumItems) {
                AddItemButton(onAddItemClicked = onAddItemClicked)
            }

            VerticalSpacerD1200()

            Button(
                onClick = onNextClicked,
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.Background,
                enabled = true,
            ) {
                Text(text = "Next Step")
            }

            VerticalSpacerD1200()
        }
    }
}

@Composable
private fun AddItemButton(
    onAddItemClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .border(
                width = Dimension.D25,
                color = OddOneOutTheme.colors.border.color,
                shape = Radii.Button.shape
            )
            .padding(
                vertical = Dimension.D500,
                horizontal = Dimension.D1000
            )
            .bounceClick(onClick = onAddItemClicked)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(spyfallIcon = SpyfallIcon.Add("Add Item"))

        HorizontalSpacerD400()

        Text(text = "Add Item", typography = OddOneOutTheme.typography.Label.L500)
    }
}

@Composable
private fun ItemsList(
    items: List<PackItem.Custom>,
    onEditItem: (PackItem.Custom) -> Unit,
) {

    items.forEachIndexed { index, item ->
        if (index != 0) {
            Spacer(modifier = Modifier.height(Dimension.D500))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    OddOneOutTheme.colors.surfacePrimary.color,
                    shape = Radii.Card.shape
                )
                .padding(horizontal = Dimension.D500)
                .padding(vertical = Dimension.D400),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.name)

                if (item.roles?.isNotEmpty() == true) {
                    Text(
                        text = "${item.roles?.size ?: 0} Roles",
                        typography = OddOneOutTheme.typography.Label.L700
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimension.D500))

            Spacer(modifier = Modifier.weight(1f))

            HorizontalSpacerD400()

            IconButton(
                icon = SpyfallIcon.Pencil("Change name"),
                onClick = {
                    onEditItem(item)
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewAddItemsScreen() {
    Preview() {
        AddItemsScreen(
            name = "My special pack",
            onNextClicked = { },
            onNavigateBack = {},
            maximumItems = 5,
            onAddItemClicked = {},
            items = listOf(
                PackItem.Custom("Item 1", roles = null),
            )
        )
    }
}
