package com.dangerfield.libraries.ui.components.radio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dangerfield.libraries.ui.Preview
import com.dangerfield.libraries.ui.bounceClick
import com.dangerfield.libraries.ui.components.text.Text
import com.dangerfield.libraries.ui.rememberRipple
import kotlin.enums.EnumEntries

@Composable
fun RadioGroup(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    direction: LayoutDirection = LayoutDirection.Vertical,
    radioButtons: RadioGroupScope.() -> Unit,
) {
    val scope = RadioGroupScopeImpl()
    scope.radioButtons()

    DirectionWrapper(modifier, direction) {
        scope.items.forEachIndexed { index, item ->
            Box(modifier = Modifier.bounceClick { onItemSelected(index) }) {
                item(index == selectedIndex)
            }
        }
    }
}

@Composable
fun <T : Enum<T>> EnumRadioGroup(
    enumEntries: EnumEntries<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    direction: LayoutDirection = LayoutDirection.Vertical,
    item: @Composable (T) -> Unit
) {

    var selectedItem: T? by remember { mutableStateOf(null) }

    DirectionWrapper(modifier, direction) {
        enumEntries.forEach {
            Box(modifier = Modifier.bounceClick {
                selectedItem = it
                onItemSelected(it)
            }) {
                item(it)
            }
        }
    }
}

@Composable
private fun DirectionWrapper(
    modifier: Modifier = Modifier,
    direction: LayoutDirection = LayoutDirection.Vertical,
    content: @Composable () -> Unit
) {
    if (direction == LayoutDirection.Vertical) {
        Column(modifier) {
            content()
        }
    } else {
        Row(modifier) {
            content()
        }
    }
}

enum class LayoutDirection { Vertical, Horizontal }

private class RadioGroupScopeImpl : RadioGroupScope {
    val items = mutableListOf<@Composable (isSelected: Boolean) -> Unit>()

    override fun Item(content: @Composable (isSelected: Boolean) -> Unit) {
        items.add(content)
    }
}

interface RadioGroupScope {
    fun Item(content: @Composable (isSelected: Boolean) -> Unit)
}

