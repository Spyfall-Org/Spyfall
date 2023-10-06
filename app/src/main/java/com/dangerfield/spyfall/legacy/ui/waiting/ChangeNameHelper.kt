package com.dangerfield.spyfall.legacy.ui.waiting

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.DialogChangeNameBinding
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.addCharacterMax
import com.dangerfield.spyfall.legacy.util.clear
import com.dangerfield.spyfall.legacy.util.goneIf
import com.dangerfield.spyfall.legacy.util.hideKeyboard
import com.dangerfield.spyfall.legacy.util.invisibleIf
import com.dangerfield.spyfall.legacy.util.openKeyboard
import com.dangerfield.spyfall.legacy.util.visibleIf

interface NameChangeEventFirer {
    fun triggerNameChangeEvent(newName: String)
    fun cancelNameChangeEvent()
}

class ChangeNameHelper(private val eventFirer: NameChangeEventFirer) {

    private var nameChangeDialog: AlertDialog? = null
    private var dialogViewBinding: DialogChangeNameBinding? = null

    fun showNameChangeDialog(context: Context) {
        nameChangeDialog = buildDialog(context)
        nameChangeDialog?.show()
        dialogViewBinding?.tvAlertChangeName?.openKeyboard()
    }

    private fun handleNameChange(newName: String) {
        eventFirer.triggerNameChangeEvent(newName)
    }

    fun updateLoadingState(loading: Boolean) {
        dialogViewBinding?.let { binding ->
            binding.tvAlertChangeName.invisibleIf(loading)
            binding.progressBar.visibleIf(loading)
            binding.btnChangeNameAlertOkay.goneIf(loading)

            val loadingSet = ConstraintSet()
            val origionalSet = ConstraintSet()
            loadingSet.clone(binding.changeNameLayout)
            origionalSet.clone(binding.changeNameLayout)

            loadingSet.clear(R.id.btn_alert_change_name_canel, ConstraintSet.END)
            loadingSet.connect(
                R.id.btn_alert_change_name_canel,
                ConstraintSet.END,
                R.id.tv_alert_change_name,
                ConstraintSet.END
            )

            TransitionManager.beginDelayedTransition(binding.changeNameLayout)
            if (loading) {
                loadingSet.applyTo(binding.changeNameLayout)
            } else {
                origionalSet.applyTo(binding.changeNameLayout)
            }
        }
    }

    private fun buildDialog(context: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogViewBinding = DialogChangeNameBinding.inflate(LayoutInflater.from(context))
        dialogBuilder.setView(dialogViewBinding?.root)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogViewBinding?.apply {

            UIHelper.updateDrawableToTheme(context, R.drawable.edit_text_custom_cursor)
            tvAlertChangeName.addCharacterMax(25)
            btnChangeNameAlertOkay.background.setTint(UIHelper.accentColor)
            progressBar.indeterminateDrawable
                .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN)
            btnChangeNameAlertOkay.setOnClickListener {
                val newName = tvAlertChangeName.text.toString().trim()
                handleNameChange(newName)
            }
            btnAlertChangeNameCanel.setOnClickListener {
                eventFirer.cancelNameChangeEvent()
                this.tvAlertChangeName.hideKeyboard()
                dialog.dismiss()
            }
        }

        return dialog
    }

    fun dismissNameChangeDialog() {
        dialogViewBinding?.tvAlertChangeName?.clear()
        dialogViewBinding?.tvAlertChangeName?.hideKeyboard()
        nameChangeDialog?.dismiss()
    }
}
