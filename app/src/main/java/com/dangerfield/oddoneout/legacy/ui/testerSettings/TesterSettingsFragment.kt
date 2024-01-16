package com.dangerfield.oddoneout.legacy.ui.testerSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.dangerfield.features.qa.internal.QaScreen
import com.dangerfield.features.qa.internal.QaViewModel
import com.dangerfield.libraries.ui.themedComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TesterSettingsFragment : Fragment() {

    private val viewModel: QaViewModel by viewModels()

    private val navController by lazy {
        NavHostFragment.findNavController(this)
    }

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
                },
                onNavigateBack = navController::popBackStack
            )
        }
    }
}