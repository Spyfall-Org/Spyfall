package com.dangerfield.libraries.ui

import androidx.compose.ui.unit.Dp

object Sizes {

    /** 2dp */
    val S50 = NumericalValues.V50

    /** 4dp */
    val S100 = NumericalValues.V100

    /** 6dp */
    val S200 = NumericalValues.V200

    /** 8dp */
    val S300 = NumericalValues.V300

    /** 10dp */
    val S400 = NumericalValues.V400

    /** 12dp */
    val S500 = NumericalValues.V500

    /** 14dp */
    val S600 = NumericalValues.V600

    /** 16dp */
    val S700 = NumericalValues.V700

    /** 20dp */
    val S800 = NumericalValues.V800

    /** 24dp */
    val S900 = NumericalValues.V900

    /** 28dp */
    val S1000 = NumericalValues.V1000

    /** 34dp */
    val S1100 = NumericalValues.V1100

    /** 40dp */
    val S1200 = NumericalValues.V1200

    /** 48dp */
    val S1300 = NumericalValues.V1300

    /** 58dp */
    val S1400 = NumericalValues.V1400

    /** 70dp */
    val S1500 = NumericalValues.V1500

    /** 84dp */
    val S1600 = NumericalValues.V1600

    fun validate(size: Dp) {
        NumericalValues.validate(size)
    }
}
