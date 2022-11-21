package com.dangerfield.spyfall.legacy.ui.waiting

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.addCharacterMax
import com.dangerfield.spyfall.legacy.util.clear
import com.dangerfield.spyfall.legacy.util.goneIf
import com.dangerfield.spyfall.legacy.util.hideKeyboard
import com.dangerfield.spyfall.legacy.util.invisibleIf
import com.dangerfield.spyfall.legacy.util.openKeyboard
import com.dangerfield.spyfall.legacy.util.visibleIf
import com.dangerfield.spyfall.legacy.util.*
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

    private fun handleNameChange(newName: String) {
        eventFirer.triggerNameChangeEvent(newName)
    }

    fun updateLoadingState(loading: Boolean) {
        dialogView?.let {dialog ->
            dialog.tv_alert_change_name.invisibleIf(loading)
            dialog.progressBar.visibleIf(loading)
            dialog.btn_change_name_alert_okay.goneIf(loading)

            val loadingSet = ConstraintSet()
            val origionalSet = ConstraintSet()
            loadingSet.clone(dialog.change_name_layout)
            origionalSet.clone(dialog.change_name_layout)

            loadingSet.clear(R.id.btn_alert_change_name_canel, ConstraintSet.END)
            loadingSet.connect(
                R.id.btn_alert_change_name_canel,
                ConstraintSet.END,
                R.id.tv_alert_change_name,
                ConstraintSet.END
            )

            TransitionManager.beginDelayedTransition(dialog.change_name_layout)
            if(loading) {
                loadingSet.applyTo(dialog.change_name_layout)
            } else {
                origionalSet.applyTo(dialog.change_name_layout)
            }
        }
    }

    private fun buildDialog(context: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_name, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialogView?.apply {

            UIHelper.updateDrawableToTheme(context, R.drawable.edit_text_custom_cursor)
            tv_alert_change_name.addCharacterMax(25)
            btn_change_name_alert_okay.background.setTint(UIHelper.accentColor)
            progressBar.indeterminateDrawable
                .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN)
            btn_change_name_alert_okay.setOnClickListener {
                val newName = tv_alert_change_name.text.toString().trim()
                handleNameChange(newName)
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