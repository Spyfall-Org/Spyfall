package com.dangerfield.spyfall.customClasses

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.alert_change_name.*
import kotlinx.android.synthetic.main.alert_change_name.view.*
import kotlinx.android.synthetic.main.alert_custom.view.*


class UIHelper {

    companion object {

        val keyboardHider = View.OnFocusChangeListener { view, b ->
            if (!b) {
                this.hideKeyboardFrom(view)
            }
        }

        private fun hideKeyboardFrom(view: View) {
            val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun simpleAlert(
            context: Context, title: String, message: String, positiveText: String, positiveAction: (() -> Unit),
            negativeText: String, negativeAction: (() -> Unit)
        ): AlertDialog {

            val dialogBuilder = AlertDialog.Builder(context)
            // set message of alert dialog
            dialogBuilder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(positiveText) { _, _ -> positiveAction.invoke() }
                .setNegativeButton(negativeText) { dialog, _ -> negativeAction.invoke(); dialog.cancel() }

            val alert = dialogBuilder.create()
            alert.setTitle(title)
            return alert
        }

        fun customAlert(
            context: Context, title: String, message: String, positiveText: String, positiveAction: (() -> Unit),
            negativeText: String, negativeAction: (() -> Unit)
        ): Dialog {

            val dialogBuilder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_custom, null)
            dialogBuilder.setView(view)
            val dialog = dialogBuilder.create()

            view.apply {
                btn_custom_alert_negative.setOnClickListener { negativeAction.invoke(); dialog.cancel() }
                btn_custom_alert_positive.setOnClickListener { positiveAction.invoke(); dialog.dismiss() }
                btn_custom_alert_negative.text = negativeText
                btn_custom_alert_positive.text = positiveText
                tv_custom_alert_message.text = message
                tv_custom_alert_title.text = title
            }

            return dialog
        }
    }

}