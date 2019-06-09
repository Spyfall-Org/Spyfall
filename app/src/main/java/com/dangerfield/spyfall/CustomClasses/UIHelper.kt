package com.dangerfield.spyfall.CustomClasses

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.dangerfield.spyfall.MainActivity
import androidx.core.content.ContextCompat.getSystemService



class UIHelper {

    companion object{

        val keyboardHider =  View.OnFocusChangeListener {view,b ->
            if(!b){
                this.hideKeyboardFrom(view)

            }
        }

        fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

    }


}