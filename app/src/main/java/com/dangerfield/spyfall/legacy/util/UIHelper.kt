package com.dangerfield.spyfall.legacy.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.DialogCustomBinding
import com.dangerfield.spyfall.databinding.DialogPacksBinding
import com.dangerfield.spyfall.databinding.DialogReviewBinding


class UIHelper {

    companion object {

        var accentColor: Int = Color.parseColor("#E3212F")

        var accentColors =
            mutableListOf(
                Color.parseColor("#9533C7"),
                Color.parseColor("#00A0EF"),
                Color.parseColor("#2FD566"),
                Color.parseColor("#FF5800"),
                Color.parseColor("#E3212F")
            )

        fun getSavedColor(context: Context) {
            val prefs = context.getSharedPreferences(
                context.resources.getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )
            val savedColor = prefs.getInt(
                context.resources.getString(R.string.shared_preferences_color),
                Color.WHITE
            )
            accentColor = if (savedColor == Color.WHITE) accentColors.random() else savedColor
        }

        fun errorDialog(context: Context) = customSimpleAlert(context,
            context.resources.getString(R.string.error_title),
            context.resources.getString(R.string.newtork_error_message),
            context.resources.getString(R.string.positive_action_standard),
            {},
            "",
            {})

        fun packsDialog(context: Context, packsList: MutableList<List<String>>): AlertDialog {
            val dialogBuilder = AlertDialog.Builder(context)
            Log.d("Custom alerts", "Setting packs view")
            val viewBinding = DialogPacksBinding.inflate(LayoutInflater.from(context))
            dialogBuilder.setView(viewBinding.root)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            //TODO: make dynamic by pulling from firebase and adding recycler views as needed
            //function would need to accept an array of lists, and cycle through them
            //TOD DO THIS WE MIGHT HAVE TO MAKE THE PARENT VIEW A LINEAR LAYOUT

            viewBinding.apply {
                tvDialogPack1Header.text = packsList[0][0]
                rvDialogPack1.adapter =
                    SimpleTextAdapter(packsList[0].subList(1, packsList[0].size), context)
                rvDialogPack1.layoutManager = GridLayoutManager(context, 2)
                rvDialogPack1.setHasFixedSize(true)

                tvDialogPack2Header.text = packsList[1][0]
                rvDialogPack2.adapter =
                    SimpleTextAdapter(packsList[1].subList(1, packsList[1].size), context)
                rvDialogPack2.layoutManager = GridLayoutManager(context, 2)
                rvDialogPack2.setHasFixedSize(true)

                tvDialogPack3Header.text = packsList[2][0]
                rvDialogPack3.adapter =
                    SimpleTextAdapter(packsList[2].subList(1, packsList[2].size), context)
                rvDialogPack3.layoutManager = GridLayoutManager(context, 2)
                rvDialogPack3.setHasFixedSize(true)

                btnDialogPacksPositive.background.setTint(accentColor)
                btnDialogPacksPositive.setOnClickListener { dialog.dismiss() }
            }

            return dialog
        }

        @Suppress("LongParameterList")
        fun customSimpleAlert(
            context: Context,
            title: String,
            message: String,
            positiveText: String,
            positiveAction: (() -> Unit),
            negativeText: String? = null,
            negativeAction: (() -> Unit) = {},
            leftAlignText: Boolean = false
        ): Dialog {

            val dialogBuilder = AlertDialog.Builder(context)
            val viewBinding = DialogCustomBinding.inflate(LayoutInflater.from(context))
            dialogBuilder.setView(viewBinding.root)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            viewBinding.apply {
                if (leftAlignText) {
                    tvCustomAlert.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                }
                if (negativeText.isNullOrEmpty()) {
                    //remove the negative button
                    btnCustomAlertNegative.visibility = View.GONE

                    val set = ConstraintSet()
                    val layout = customAlertView
                    set.clone(layout)
                    // remove all connections.
                    set.clear(R.id.btn_custom_alert_positive, ConstraintSet.END)
                    set.clear(R.id.btn_custom_alert_positive, ConstraintSet.START)
                    //center it
                    set.connect(
                        R.id.btn_custom_alert_positive,
                        ConstraintSet.END,
                        R.id.custom_alert_view,
                        ConstraintSet.END
                    )
                    set.connect(
                        R.id.btn_custom_alert_positive,
                        ConstraintSet.START,
                        R.id.custom_alert_view,
                        ConstraintSet.START
                    )
                    set.applyTo(layout)
                }

                if (title.trim() == context.resources.getString(R.string.about_title)) {
                    btnEmail.visibility = View.VISIBLE
                    btnEmail.setLinkTextColor(accentColor)
                }

                btnCustomAlertNegative.setOnClickListener { negativeAction.invoke(); dialog.cancel() }
                btnCustomAlertPositive.setOnClickListener { positiveAction.invoke(); dialog.dismiss() }
                btnCustomAlertNegative.text = negativeText
                btnCustomAlertPositive.text = positiveText
                //for theme changing
                btnCustomAlertPositive.background.setTint(accentColor)
                tvCustomAlert.text = message

                tvCustomAlertTitle.text = title
            }
            return dialog
        }

        fun getReviewDialog(
            context: Context,
            positiveAction: (() -> Unit),
            negativeAction: (() -> Unit) = {}
        ): AlertDialog {
            val dialogBuilder = AlertDialog.Builder(context)
            val viewBinding = DialogReviewBinding.inflate(LayoutInflater.from(dialogBuilder.context))
            dialogBuilder.setView(viewBinding.root)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true)

            viewBinding.apply {
                btnPositive.setOnClickListener { positiveAction.invoke(); dialog.cancel() }
                btnNegative.setOnClickListener { negativeAction.invoke(); dialog.dismiss() }
                //for theme changing
                btnPositive.background.setTint(accentColor)
            }
            return dialog
        }

        fun getForcedUpdateDialog(
            context: Context,
            positiveAction: () -> Unit
        ): Dialog {
            return customSimpleAlert(
                context = context,
                title = context.getString(R.string.update_title),
                message = context.getString(R.string.update_message),
                positiveText = context.getString(R.string.positive_action_standard),
                positiveAction = positiveAction,
                leftAlignText = false
            )
        }

        fun updateDrawableToTheme(context: Context, id: Int) {
            val unwrappedDrawable =
                AppCompatResources.getDrawable(context, id)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, accentColor)
        }
    }
}
