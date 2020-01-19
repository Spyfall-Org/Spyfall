package com.dangerfield.spyfall.start

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.fragment_start.*
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


class StartFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcome_message.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

        navController = NavHostFragment.findNavController(this)

        btn_new_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_newGameFragment)
        }

        btn_join_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_joinGameFragment)
        }

        btn_rules.setOnClickListener{
            UIHelper.customSimpleAlert(context!!,
                resources.getString(R.string.rules_title),
                resources.getString(R.string.rules_message),
                resources.getString(R.string.positive_action_standard)
                ,{}, "",{}).show()
        }

        btn_settings.setOnClickListener{
            navController.navigate(R.id.action_startFragment_to_settingsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        UIHelper.getSavedColor(context!!)
        changeAccent()

        //increment number of games played, if it is a multiple of 5 and the user hasnt reviewed yet, ask for a review
        if(incrementGamePlay() % 5 == 0 && !userHasReviewed()){
            //show request for review
            UIHelper.customSimpleAlert(context!!,
                getString(R.string.dialog_rate_title),
                getString(R.string.dialog_rate_message),
                getString(R.string.positive_action_standard),{
                    openStoreForReview()
                    setUserReview()
                },getString(R.string.dialog_rate_negative),{}).show()
        }
    }

    fun changeAccent(){
        btn_join_game.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_rules.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        btn_rules.setTextColor(UIHelper.accentColor)

        DrawableCompat.setTint(
            DrawableCompat.wrap(btn_settings.drawable),
            ContextCompat.getColor(context!!, R.color.colorTheme)
        )
    }

    //TODO; make a prefrences object that deal with all of this
    private fun incrementGamePlay(): Int{
        val editor = context!!.getSharedPreferences(resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        ).edit()

        val newVal = getGamesPlayed() + 1
        editor.putInt(resources.getString(R.string.shared_preferences_games), newVal)
        editor.apply()

        return newVal
    }

    private fun setUserReview(){
        val editor = context!!.getSharedPreferences(resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        ).edit()
        editor.putBoolean(resources.getString(R.string.shared_preferences_hasReviewed), true)
        editor.apply()
    }

    fun getGamesPlayed(): Int{
        val prefs = context!!.getSharedPreferences(context!!.resources.getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        return prefs.getInt(context!!.resources.getString(R.string.shared_preferences_games), 0)
    }

    fun userHasReviewed(): Boolean {
        val prefs = context!!.getSharedPreferences(context!!.resources.getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        return prefs.getBoolean(context!!.resources.getString(R.string.shared_preferences_hasReviewed), false)
    }

    fun openStoreForReview(){
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try
        {
            startActivity(goToMarket)
        }
        catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)))
        }

    }
}
