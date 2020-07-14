package com.dangerfield.spyfall.waiting

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.clear
import com.dangerfield.spyfall.util.hideKeyboard
import com.dangerfield.spyfall.util.openKeyboard
import kotlinx.android.synthetic.main.alert_change_name.view.*

interface NameChangeEventFirer {
    fun fireNameChangeEvent(newName: String)
}

class ChangeNameHelper(private val eventFirer: NameChangeEventFirer) {

    private var nameChangeDialog: AlertDialog? = null
    private var dialogView: View? = null;

    fun showNameChangeDialog(context: Context) {
        if (nameChangeDialog == null) nameChangeDialog = buildDialog(context)
        nameChangeDialog?.show()
        dialogView?.tv_alert_change_name?.openKeyboard()
    }

    private fun handleNameChange(newName: String) {
        eventFirer.fireNameChangeEvent(newName)
    }

    private fun buildDialog(context: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        if (dialogView == null) dialogView =
            LayoutInflater.from(context).inflate(R.layout.alert_change_name, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialogView?.apply {
            btn_change_name_alert_okay.background.setTint(UIHelper.accentColor)
            UIHelper.setCursorColor(tv_alert_change_name, UIHelper.accentColor)
            btn_change_name_alert_okay.setOnClickListener {
                val newName = tv_alert_change_name.text.toString().trim()
                handleNameChange(newName)
            }
            btn_alert_change_name_canel.setOnClickListener {
                this.tv_alert_change_name.hideKeyboard()
                dialog.dismiss()
            }
        }

        return dialog
    }

    fun dismissNameChangeDialog() {
        dialogView?.tv_alert_change_name?.clear()
        dialogView?.tv_alert_change_name?.hideKeyboard()
        nameChangeDialog?.dismiss()
    }
}