package com.dangerfield.spyfall.CustomClasses

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


class UIHelper {

    companion object{

        val keyboardHider =  View.OnFocusChangeListener {view,b ->
            if(!b){ this.hideKeyboardFrom(view) }
        }

        fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}