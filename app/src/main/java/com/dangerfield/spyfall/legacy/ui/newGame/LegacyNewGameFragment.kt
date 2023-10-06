package com.dangerfield.spyfall.legacy.ui.newGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.databinding.FragmentNewGameLegacyBinding
import com.dangerfield.spyfall.legacy.api.Resource
import com.dangerfield.spyfall.legacy.models.Session
import com.dangerfield.spyfall.legacy.ui.waiting.LegacyWaitingFragment
import com.dangerfield.spyfall.legacy.util.EventObserver
import com.dangerfield.spyfall.legacy.util.LogHelper
import com.dangerfield.spyfall.legacy.util.UIHelper
import com.dangerfield.spyfall.legacy.util.addCharacterMax
import com.dangerfield.spyfall.legacy.util.goneIf
import com.dangerfield.spyfall.legacy.util.setHideKeyBoardOnPressAway
import com.dangerfield.spyfall.legacy.util.viewBinding
import com.dangerfield.spyfall.legacy.util.visibleIf
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegacyNewGameFragment : Fragment(R.layout.fragment_new_game_legacy) {

    private val newGameViewModel: NewGameViewModel by viewModel()
    private val packsAdapter: PacksAdapter by lazy { PacksAdapter(newGameViewModel.getPacks()) }
    private val navController by lazy { NavHostFragment.findNavController(this) }
    private val binding by viewBinding(FragmentNewGameLegacyBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    newGameViewModel.cancelPendingOperations()
                    navController.popBackStack(R.id.startFragment, false)
                }
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeCreateGameEvent()
        observeShowPacksEvent()
    }

    private fun setupView() {
        changeAccent()
        with(binding) {
            tvNewGameName.setHideKeyBoardOnPressAway()
            tvNewGameTime.setHideKeyBoardOnPressAway()
            tvNewGameTime.addCharacterMax(2)
            tvNewGameTime.addCharacterMax(25)
            btnCreate.setOnClickListener { triggerCreateGameEvent() }
            btnPacks.setOnClickListener { triggerGetPackDetailsEvent() }
        }
        configurePacksAdapter()
    }

    private fun observeShowPacksEvent() {
        newGameViewModel.getShowPackEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                if (!this.isAdded) return@EventObserver
                when (it) {
                    is Resource.Success -> it.data?.let { d -> handlePacksDetailsSuccess(d) }
                    is Resource.Error -> handlePacksDetailsError(it)
                }
                showLoadingForGettingPacks(false)
            }
        )
    }

    private fun observeCreateGameEvent() {
        newGameViewModel.getCreateGameEvent().observe(
            viewLifecycleOwner,
            EventObserver {
                if (!this.isAdded) return@EventObserver
                when (it) {
                    is Resource.Success -> it.data?.let { session ->
                        handleSucessfulGameCreation(
                            session
                        )
                    }

                    is Resource.Error -> handleErrorCreatingGame(it)
                }
            }
        )
    }

    private fun triggerCreateGameEvent() {
        showLoadingForCreateGame(true)
        val timeLimit = binding.tvNewGameTime.text.toString().trim()
        val playerName = binding.tvNewGameName.text.toString().trim()
        val chosenPacks =
            packsAdapter.packs.filter { it.isSelected }.map { it.queryString } as ArrayList<String>
        newGameViewModel.triggerCreateGameEvent(playerName, timeLimit, chosenPacks)
    }

    private fun triggerGetPackDetailsEvent() {
        showLoadingForGettingPacks(true)
        newGameViewModel.triggerGetPackDetailsEvent()
    }

    private fun handleErrorCreatingGame(result: Resource.Error<Session, NewGameError>) {
        showLoadingForCreateGame(false)
        result.exception?.let { LogHelper.logErrorCreatingGame(it) }
        result.error?.let { error ->
            if (error == NewGameError.NETWORK_ERROR) {
                UIHelper.errorDialog(requireContext()).show()
            } else {
                error.resId?.let {
                    Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleSucessfulGameCreation(currentSession: Session) {
        showLoadingForCreateGame(false)
        val bundle = Bundle()
        bundle.putParcelable(LegacyWaitingFragment.SESSION_KEY, currentSession)
        if (navController.currentDestination?.id == R.id.newGameFragment) {
            navController.navigate(R.id.action_newGameFragment_to_waitingFragment, bundle)
        }
    }

    private fun handlePacksDetailsError(result: Resource.Error<List<List<String>>, PackDetailsError>) {
        result.exception?.let { LogHelper.logErrorGettingPacksDetails(it) }

        result.error?.let { error ->
            if (error == PackDetailsError.NETWORK_ERROR) {
                UIHelper.errorDialog(requireContext()).show()
            } else {
                error.resId?.let {
                    Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handlePacksDetailsSuccess(data: List<List<String>>) {
        UIHelper.packsDialog(requireContext(), data as MutableList<List<String>>).show()
    }

    private fun showLoadingForCreateGame(loading: Boolean) {
        with(binding) {
            pbNewGame.goneIf(!loading)
            btnCreate.text = if (loading) "" else getString(R.string.string_btn_create)
            btnCreate.isClickable = !loading
            btnPacks.isClickable = !loading
        }
    }

    private fun showLoadingForGettingPacks(loading: Boolean) {
        with(binding) {
            pbPacks.visibleIf(loading)
            btnPacks.visibleIf(!loading)
            btnPacks.isClickable = !loading
        }
    }

    private fun configurePacksAdapter() {
        binding.rvPacks.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = packsAdapter
            setHasFixedSize(true)
        }
    }

    private fun changeAccent() {
        binding.btnCreate.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_info).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        binding.btnPacks.setImageDrawable(drawable)

        UIHelper.updateDrawableToTheme(requireContext(), R.drawable.edit_text_custom_cursor)

        binding.pbPacks.indeterminateDrawable
            .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN)

        binding.pbNewGame.indeterminateDrawable
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }
}
