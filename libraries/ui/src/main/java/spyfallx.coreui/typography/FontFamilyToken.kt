package spyfallx.ui.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import spyfallx.ui.R

internal enum class FontFamilyToken(val fontFamily: FontFamily) {
    Poppins(
        FontFamily(
            Font(
                resId = R.font.poppins_light,
                weight = FontWeight.Light
            ),
            Font(
                resId = R.font.poppins_regular,
                weight = FontWeight.Normal
            ),
            Font(
                resId = R.font.poppins_medium,
                weight = FontWeight.Medium
            ),
            Font(
                resId = R.font.poppins_bold,
                weight = FontWeight.Bold
            ),
            Font(
                resId = R.font.poppins_semibold,
                weight = FontWeight.SemiBold
            )
        )
    )
}
