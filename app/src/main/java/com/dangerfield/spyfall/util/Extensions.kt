package com.dangerfield.spyfall.util

import android.text.InputFilter
import android.widget.EditText

fun EditText.addCharacterMax(max: Int){
    filters = arrayOf(InputFilter.LengthFilter(max))
}