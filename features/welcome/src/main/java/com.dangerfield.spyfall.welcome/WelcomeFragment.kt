package com.dangerfield.spyfall.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.dangerfield.spyfall.settingsapi.SettingsFragmentFactory
import com.dangerfield.spyfall.welcome.databinding.FragmentWelcomeBinding
import com.dangerfield.spyfall.welcomeapi.WelcomeFragmentFactory
import org.koin.android.ext.android.inject
import spyfallx.coreui.viewBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private val welcomeNavigator : WelcomeNavigator by inject()
    private val viewBinding = viewBinding(FragmentWelcomeBinding::inflate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        viewBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.view

    }


    companion object : WelcomeFragmentFactory {
        override fun newInstance() = WelcomeFragment()
    }
}