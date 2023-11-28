package com.dangerfield.spyfall.legacy.ui.testerSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dangerfield.features.qa.internal.QaScreen
import com.dangerfield.features.qa.internal.QaViewModel
import com.dangerfield.libraries.ui.themedComposeView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.FragmentTesterSettingsBinding
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.util.DBCleaner
import com.dangerfield.spyfall.legacy.util.PreferencesService
import com.dangerfield.spyfall.legacy.util.viewBinding
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class TesterSettingsFragment : Fragment() {

    private val viewModel: QaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.context.themedComposeView {

            val state by viewModel.state.collectAsStateWithLifecycle()

            QaScreen(
                configuredValues = state.configValues,
                experiments = state.experiments,
                onExperimentOverride = { experiment, value ->
                    viewModel.addOverride(experiment.path, value)
                }
            )
        }
    }
}
