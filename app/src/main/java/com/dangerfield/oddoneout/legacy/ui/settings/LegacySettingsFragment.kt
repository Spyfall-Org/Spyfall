package com.dangerfield.oddoneout.legacy.ui.settings

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.oddoneout.BuildConfig
import com.dangerfield.oddoneout.R
import com.dangerfield.oddoneout.databinding.DialogCustomBinding
import com.dangerfield.oddoneout.databinding.FragmentSettingsLegacyBinding
import com.dangerfield.oddoneout.legacy.util.FeedbackHelper
import com.dangerfield.oddoneout.legacy.util.UIHelper
import com.dangerfield.oddoneout.legacy.util.goneIf
import com.dangerfield.oddoneout.legacy.util.viewBinding
import org.koin.android.ext.android.inject

class LegacySettingsFragment : Fragment(), ColorChangeAdapter.ColorChanger {

    override fun onColorChange(colorButton: ColorButton) {
        val anim = ValueAnimator.ofArgb(UIHelper.accentColor, colorButton.color)
        anim.addUpdateListener {
            colorChanger.findViewById<Button>(R.id.btn_custom_alert_positive)
                .background
                .setTint(it.animatedValue as Int)
        }
        anim.duration = 300
        anim.start()
    }

    private val colorChanger: AlertDialog by lazy { getColorChangeDialog() }
    private val feedbackHelper: FeedbackHelper by inject()

    private val currentMode: Int
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    private var newMode: Int? = null

    private val binding by viewBinding(FragmentSettingsLegacyBinding::bind)

    lateinit var colors: MutableList<ColorButton>
    lateinit var colorChangeAdapter: ColorChangeAdapter
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        colors = mutableListOf()
        UIHelper.accentColors.forEach { colors.add(ColorButton(it, false)) }
        // this is a flag for random colors
        colors.add(ColorButton(Color.WHITE, false))
        colorChangeAdapter = ColorChangeAdapter(colors, context, this)

        return inflater.inflate(R.layout.fragment_settings_legacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireParentFragment().requireView())
        binding.btnThemeChange.setOnClickListener { colorChanger.show() }

        binding.btnAbout.setOnClickListener {
            UIHelper.customSimpleAlert(
                requireContext(),
                resources.getString(R.string.about_title),
                resources.getString(R.string.about_message),
                resources.getString(R.string.positive_action_standard), {}, "", {}
            ).show()
        }

        binding.btnTesterSettings.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_testerSettingsFragment)
        }

        binding.btnFeedback.setOnClickListener {
            feedbackHelper.showFeedbackDialog(requireContext())
        }

        showTesterSettings(BuildConfig.DEBUG)
    }

    private fun showTesterSettings(debug: Boolean) {
        binding.btnTesterSettings.goneIf(!debug)
        binding.tvTesterSettings.goneIf(!debug)
        binding.tvTesterSettings.goneIf(!debug)
    }

    override fun onResume() {
        super.onResume()
        setTheme()
    }

    private fun getColorChangeDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogCustomBinding.inflate(LayoutInflater.from(dialogBuilder.context))
        dialogBuilder.setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)

        dialogBinding.apply {

            rvColorChange.visibility = View.VISIBLE

            if (Integer.valueOf(android.os.Build.VERSION.SDK) < 29) { // below api 29 does not have system dark mode
                modeToggle.visibility = View.VISIBLE
                if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
                    modeToggle.check(R.id.tgl_dark_mode)
                } else {
                    modeToggle.check(R.id.tgl_light_mode)
                }
                modeToggle.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.tgl_light_mode -> newMode = AppCompatDelegate.MODE_NIGHT_NO
                        R.id.tgl_dark_mode -> newMode = AppCompatDelegate.MODE_NIGHT_YES
                    }
                }
            }

            rvColorChange.adapter = colorChangeAdapter
            rvColorChange.layoutManager = GridLayoutManager(context, 4)
            rvColorChange.setHasFixedSize(true)

            btnCustomAlertNegative.setOnClickListener { dialog.cancel() }

            btnCustomAlertPositive.setOnClickListener {
                if (colorChangeAdapter.selectedPosition != -1) {
                    val chosenColor =
                        colorChangeAdapter.colors[colorChangeAdapter.selectedPosition].color
                    // color white is the flag for random colors
                    UIHelper.accentColor = if (chosenColor == Color.WHITE) {
                        UIHelper.accentColors.random()
                    } else {
                        chosenColor
                    }
                    saveColor(chosenColor)
                }
                newMode?.let { AppCompatDelegate.setDefaultNightMode(it) }
                dialog.dismiss()
            }
            btnCustomAlertNegative.text =
                resources.getString(R.string.negative_action_standard)
            btnCustomAlertPositive.text =
                resources.getString(R.string.theme_change_positive)
            // for theme changing
            btnCustomAlertPositive.background.setTint(UIHelper.accentColor)
            tvCustomAlert.text = resources.getString(R.string.theme_change_message)
            tvCustomAlertTitle.text = resources.getString(R.string.theme_change_title)
        }
        return dialog
    }

    private fun saveColor(chosenColor: Int) {
        val editor = requireContext()
            .getSharedPreferences(
                resources.getString(R.string.shared_preferences), MODE_PRIVATE
            ).edit()
        editor.putInt(resources.getString(R.string.shared_preferences_color), chosenColor)
        editor.apply()
    }

    private fun setTheme() {
        listOf(
            binding.ivTheme,
            binding.icAbout,
            binding.ivTesterSettings,
            binding.ivFeedback
        ).forEach {
            DrawableCompat.setTint(
                DrawableCompat.wrap(it.drawable),
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }
    }
}
