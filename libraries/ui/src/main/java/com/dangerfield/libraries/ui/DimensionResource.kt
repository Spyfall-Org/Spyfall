package com.dangerfield.libraries.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class DimensionResource(val dp: Dp) {
    data object D25 : DimensionResource(1.dp)
    data object D50 : DimensionResource(2.dp)
    data object D100 : DimensionResource(4.dp)
    data object D200 : DimensionResource(6.dp)
    data object D300 : DimensionResource(8.dp)
    data object D400 : DimensionResource(10.dp)
    data object D500 : DimensionResource(12.dp)
    data object D600 : DimensionResource(14.dp)
    data object D700 : DimensionResource(16.dp)
    data object D800 : DimensionResource(20.dp)
    data object D900 : DimensionResource(24.dp)
    data object D1000 : DimensionResource(28.dp)
    data object D1100 : DimensionResource(34.dp)
    data object D1200 : DimensionResource(40.dp)
    data object D1300 : DimensionResource(48.dp)
    data object D1400 : DimensionResource(58.dp)
    data object D1500 : DimensionResource(70.dp)
    data object D1600 : DimensionResource(84.dp)
}

object Dimension {
    val D25 = DimensionResource.D25.dp    // 1 dp
    val D50 = DimensionResource.D50.dp    // 2 dp
    val D100 = DimensionResource.D100.dp  // 4 dp
    val D200 = DimensionResource.D200.dp  // 6 dp
    val D300 = DimensionResource.D300.dp  // 8 dp
    val D400 = DimensionResource.D400.dp  // 10 dp
    val D500 = DimensionResource.D500.dp  // 12 dp
    val D600 = DimensionResource.D600.dp  // 14 dp
    val D700 = DimensionResource.D700.dp  // 16 dp
    val D800 = DimensionResource.D800.dp  // 20 dp
    val D900 = DimensionResource.D900.dp  // 24 dp
    val D1000 = DimensionResource.D1000.dp // 28 dp
    val D1100 = DimensionResource.D1100.dp // 34 dp
    val D1200 = DimensionResource.D1200.dp // 40 dp
    val D1300 = DimensionResource.D1300.dp // 48 dp
    val D1400 = DimensionResource.D1400.dp // 58 dp
    val D1500 = DimensionResource.D1500.dp // 70 dp
    val D1600 = DimensionResource.D1600.dp // 84 dp
}

fun Dp.sp(): TextUnit = this.value.sp