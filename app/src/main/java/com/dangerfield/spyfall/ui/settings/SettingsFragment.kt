package com.dangerfield.spyfall.ui.settings

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.BuildConfig
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.FeedbackHelper
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.util.goneIf
import kotlinx.android.synthetic.main.dialog_custom.*
import kotlinx.android.synthetic.main.dialog_custom.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment(), ColorChangeAdapter.ColorChanger {

    override fun onColorChange(colorButton: ColorButton) {
        val anim = ValueAnimator.ofArgb(UIHelper.accentColor, colorButton.color)
        anim.addUpdateListener {
            colorChanger.btn_custom_alert_positive.background.setTint(it.animatedValue as Int)
        }
        anim.duration = 300;
        anim.start();
    }

    private val colorChanger : AlertDialog by lazy { getColorChangeDialog()}
    private val feedbackHelper: FeedbackHelper by inject()

    private val currentMode : Int
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    private var newMode : Int? = null

    lateinit var colors: MutableList<ColorButton>
    lateinit var colorChangeAdapter: ColorChangeAdapter
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        colors = mutableListOf()
        UIHelper.accentColors.forEach { colors.add(ColorButton(it,false)) }
        //this is a flag for random colors
        colors.add(ColorButton(Color.WHITE,false))
        colorChangeAdapter = ColorChangeAdapter(colors, context, this)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireParentFragment().requireView())
        btn_theme_change.setOnClickListener{ colorChanger.show() }

        btn_about.setOnClickListener{
            UIHelper.customSimpleAlert(requireContext(),
                resources.getString(R.string.about_title),
                resources.getString(R.string.about_message),
                resources.getString(R.string.positive_action_standard),{}, "",{}).show()
        }
        if(BuildConfig.FLAVOR == "free"){
            btn_ads.setOnClickListener{
                UIHelper.customSimpleAlert(requireContext(),
                    resources.getString(R.string.ads_title),
                    resources.getString(R.string.remove_ads_message),
                    resources.getString(R.string.ads_positive_action),{sendUserToPaidVersion()},
                    resources.getString(R.string.negative_action_standard),{}).show()
            }
        }else{
            //if the user is using the paid version lets just make sure they cant see this
            btn_ads.visibility = View.GONE
            tv_ads.visibility = View.GONE
            ic_ads.visibility = View.GONE
        }

        btn_tester_settings.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_testerSettingsFragment)
        }

        btn_feedback.setOnClickListener {
            feedbackHelper.showFeedbackDialog(requireContext())
        }

        showTesterSettings(BuildConfig.DEBUG)
    }

    private fun showTesterSettings(debug: Boolean) {
        btn_tester_settings.goneIf(!debug)
        iv_tester_settings.goneIf(!debug)
        tv_tester_settings.goneIf(!debug)
    }

    override fun onResume() {
        super.onResume()
        setTheme()
    }

    private fun sendUserToPaidVersion(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=com.dangerfield.spyfall.paid")
        try
        {
            startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            Log.d("Tag", "Error launching to paid version")
        }
    }
    //TODO: consider making a view model for this
    private fun getColorChangeDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        dialogBuilder.setView(view)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true)

        view.apply {

            rv_color_change.visibility = View.VISIBLE

            if( Integer.valueOf(android.os.Build.VERSION.SDK) < 29) { //below api 29 does not have system dark mode
                mode_toggle.visibility = View.VISIBLE
                if(currentMode == Configuration.UI_MODE_NIGHT_YES){
                    mode_toggle.check(R.id.tgl_dark_mode)
                }else{
                    mode_toggle.check(R.id.tgl_light_mode)
                }
                mode_toggle.setOnCheckedChangeListener { _, checkedId ->
                    when(checkedId) {
                        R.id.tgl_light_mode -> newMode = AppCompatDelegate.MODE_NIGHT_NO
                        R.id.tgl_dark_mode -> newMode = AppCompatDelegate.MODE_NIGHT_YES
                    }
                }
            }

            rv_color_change.adapter = colorChangeAdapter
            rv_color_change.layoutManager = GridLayoutManager(context,4)
            rv_color_change.setHasFixedSize(true)

            btn_custom_alert_negative.setOnClickListener { dialog.cancel() }

            btn_custom_alert_positive.setOnClickListener {
                if(colorChangeAdapter.selectedPosition != -1) {
                    val chosenColor = colorChangeAdapter.colors[colorChangeAdapter.selectedPosition].color
                    //color white is the flag for random colors
                    UIHelper.accentColor = if(chosenColor == Color.WHITE) UIHelper.accentColors.random() else chosenColor
                    saveColor(chosenColor)
                }
                newMode?.let { AppCompatDelegate.setDefaultNightMode(it) }
                dialog.dismiss()
            }
            btn_custom_alert_negative.text = resources.getString(R.string.negative_action_standard)
            btn_custom_alert_positive.text = resources.getString(R.string.theme_change_positive)
            //for theme changing
            btn_custom_alert_positive.background.setTint(UIHelper.accentColor)
            tv_custom_alert.text = resources.getString(R.string.theme_change_message)
            tv_custom_alert_title.text = resources.getString(R.string.theme_change_title)

        }
        return dialog
    }

    private fun saveColor(chosenColor: Int){
        val editor = requireContext().getSharedPreferences(resources.getString(com.dangerfield.spyfall.R.string.shared_preferences), MODE_PRIVATE).edit()
        editor.putInt(resources.getString(R.string.shared_preferences_color), chosenColor)
        editor.apply()
    }

    private fun setTheme() {
        listOf(iv_theme, ic_about, ic_ads, iv_tester_settings, iv_feedback).forEach {
            DrawableCompat.setTint(
                DrawableCompat.wrap(it.drawable),
                ContextCompat.getColor(requireContext(), R.color.colorTheme)
            )
        }
    }
}
