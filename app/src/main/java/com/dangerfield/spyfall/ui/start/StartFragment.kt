package com.dangerfield.spyfall.ui.start

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.util.UIHelper
import kotlinx.android.synthetic.main.fragment_start.*
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dangerfield.spyfall.util.ReviewManager
import org.koin.android.ext.android.inject


class StartFragment : Fragment(R.layout.fragment_start) {

    private val navController by lazy {
        NavHostFragment.findNavController(this)
    }

    private val reviewHelper : ReviewManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcome_message.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

        btn_new_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_newGameFragment)
        }

        btn_join_game.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_joinGameFragment)
        }

        btn_rules.setOnClickListener {
            UIHelper.customSimpleAlert(requireContext(),
                resources.getString(R.string.rules_title),
                resources.getString(R.string.rules_message),
                resources.getString(R.string.positive_action_standard)
                , {}, "", {}).show()
        }

        btn_settings.setOnClickListener {
            navController.navigate(R.id.action_startFragment_to_settingsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        UIHelper.getSavedColor(requireContext())
        changeAccent()

        if (reviewHelper.shouldPromptForReview()) {
            //show request for review
            UIHelper.customSimpleAlert(requireContext(),
                getString(R.string.dialog_rate_title),
                getString(R.string.dialog_rate_message),
                getString(R.string.positive_action_standard), {
                    openStoreForReview()
                    reviewHelper.setHasClickedToReview()
                }, getString(R.string.dialog_rate_negative), {}).show()
        }
    }

    private fun changeAccent() {
        btn_join_game.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_rules.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        btn_rules.setTextColor(UIHelper.accentColor)

        DrawableCompat.setTint(
            DrawableCompat.wrap(btn_settings.drawable),
            ContextCompat.getColor(requireContext(), R.color.colorTheme)
        )
    }


    private fun openStoreForReview() {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                )
            )
        }
    }
}
