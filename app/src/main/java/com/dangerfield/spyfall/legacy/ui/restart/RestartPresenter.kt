package com.dangerfield.spyfall.legacy.ui.restart

import androidx.fragment.app.Fragment
import com.dangerfield.spyfall.databinding.FragmentRestartBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class RestartPresenter @Inject constructor(
    fragment: Fragment
) {
    @Suppress("UnusedPrivateMember")
    private val binding = FragmentRestartBinding.bind(fragment.requireView())
}
