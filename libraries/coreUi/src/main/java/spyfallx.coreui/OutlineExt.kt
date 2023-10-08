package spyfallx.coreui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.translate
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path


fun Outline.translate(dx: Float = 0f, dy: Float = 0f): Outline = translate(Offset(dx, dy))

fun Outline.translate(offset: Offset): Outline = when (this) {
    is Outline.Generic -> Outline.Generic(Path().apply { addPath(path, offset) })
    is Outline.Rectangle -> Outline.Rectangle(rect.translate(offset))
    is Outline.Rounded -> Outline.Rounded(roundRect.translate(offset))
}
