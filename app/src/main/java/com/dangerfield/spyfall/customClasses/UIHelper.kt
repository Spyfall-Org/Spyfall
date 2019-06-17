package com.dangerfield.spyfall.customClasses

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog


class UIHelper {

    companion object{

        val keyboardHider =  View.OnFocusChangeListener {view,b ->
            if(!b){ this.hideKeyboardFrom(view) }
        }

        private fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun simpleAlert(context: Context, title: String, message: String, positiveText: String, positiveAction: (()-> Unit),
                       negativeText: String, negativeAction: (()-> Unit)): AlertDialog {

            val dialogBuilder = AlertDialog.Builder(context)
            // set message of alert dialog
            dialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveText) {_, _ -> positiveAction.invoke() }
                .setNegativeButton(negativeText){ dialog,_ -> dialog.cancel() ; negativeAction.invoke() }

            val alert = dialogBuilder.create()
            alert.setTitle(title)
            return alert
        }
    }
}