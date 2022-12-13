package com.dangerfield.spyfall.splash.forcedupdate

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.splash.databinding.FragmentForcedUpdateBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ForcedUpdatePresenter @Inject constructor(
    fragment: Fragment
) {

    private val binding = FragmentForcedUpdateBinding.bind(fragment.requireView())

    init {
        binding.btnPositive.setOnClickListener {
            openStoreForReview(fragment.requireContext())
        }
    }

    private fun openStoreForReview(context: Context) {
        try {
            val uri = Uri.parse("market://details?id=" + context.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, goToMarket, Bundle())
        } catch (e: ActivityNotFoundException) {
            val uri = Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, goToMarket, Bundle())
        }
    }
}
