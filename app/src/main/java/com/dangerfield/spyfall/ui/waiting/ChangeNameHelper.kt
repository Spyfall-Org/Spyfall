package com.dangerfield.spyfall.ui.waiting

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.transition.TransitionManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.clear
import com.dangerfield.spyfall.util.hideKeyboard
import com.dangerfield.spyfall.util.openKeyboard
import kotlinx.android.synthetic.main.dialog_change_name.view.*


interface NameChangeEventFirer {
    fun triggerNameChangeEvent(newName: String)
    fun cancelNameChangeEvent()
}

class ChangeNameHelper(private val eventFirer: NameChangeEventFirer) {

    private var nameChangeDialog: AlertDialog? = null
    private var dialogView: View? = null;

    fun showNameChangeDialog(context: Context) {
        nameChangeDialog = buildDialog(context)
        nameChangeDialog?.show()
        dialogView?.tv_alert_change_name?.openKeyboard()
    }

    private fun handleNameChange(newName: String, dialog: View) {
        showLoadingState(dialog)
        eventFirer.triggerNameChangeEvent(newName)
    }

    private fun showLoadingState(dialog: View) {
        dialog.tv_alert_change_name.visibility = View.INVISIBLE
        dialog.progressBar.visibility = View.VISIBLE
        dialog.btn_change_name_alert_okay.visibility = View.GONE

        val set = ConstraintSet()
        set.clone(dialog.change_name_layout)
        set.clear(R.id.btn_alert_change_name_canel, ConstraintSet.END)
        set.connect(
            R.id.btn_alert_change_name_canel,
            ConstraintSet.END,
            R.id.tv_alert_change_name,
            ConstraintSet.END
        )
        TransitionManager.beginDelayedTransition(dialog.change_name_layout)
        set.applyTo(dialog.change_name_layout)
    }

    private fun buildDialog(context: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_name, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialogView?.apply {

            UIHelper.updateDrawableToTheme(context, R.drawable.edit_text_custom_cursor)
            btn_change_name_alert_okay.background.setTint(UIHelper.accentColor)
            progressBar.indeterminateDrawable
                .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN)
            btn_change_name_alert_okay.setOnClickListener {
                val newName = tv_alert_change_name.text.toString().trim()
                handleNameChange(newName, this)
            }
            btn_alert_change_name_canel.setOnClickListener {
                eventFirer.cancelNameChangeEvent()
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