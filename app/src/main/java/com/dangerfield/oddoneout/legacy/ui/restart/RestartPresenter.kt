package com.dangerfield.oddoneout.legacy.ui.restart

import androidx.fragment.app.Fragment
import com.dangerfield.oddoneout.databinding.FragmentRestartBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class RestartPresenter @Inject constructor(
    fragment: Fragment
) {
    @Suppress("UnusedPrivateMember")
    private val binding = FragmentRestartBinding.bind(fragment.requireView())
}
