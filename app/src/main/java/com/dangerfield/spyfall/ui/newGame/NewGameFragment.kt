package com.dangerfield.spyfall.ui.newGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.*
import kotlinx.android.synthetic.main.fragment_new_game.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class NewGameFragment : Fragment(R.layout.fragment_new_game) {

    private val newGameViewModel: NewGameViewModel by viewModel()
    private val packsAdapter: PacksAdapter by lazy { PacksAdapter(newGameViewModel.getPacks()) }
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    newGameViewModel.cancelPendingOperations()
                    navController.popBackStack(R.id.startFragment, false)
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeCreateGameEvent()
        observeShowPacksEvent()
    }

    private fun setupView() {
        changeAccent()
        tv_new_game_name.setHideKeyBoardOnPressAway()
        tv_new_game_time.setHideKeyBoardOnPressAway()
        tv_new_game_time.addCharacterMax(2)
        tv_new_game_name.addCharacterMax(25)
        btn_create.setOnClickListener { triggerCreateGameEvent() }
        btn_packs.setOnClickListener { triggerGetPackDetailsEvent() }
        configurePacksAdapter()
    }

    private fun observeShowPacksEvent() {
        newGameViewModel.getShowPackEvent().observe(viewLifecycleOwner, EventObserver {
            if (!this.isAdded) return@EventObserver
            when (it) {
                is Resource.Success -> it.data?.let { d -> handlePacksDetailsSuccess(d) }
                is Resource.Error -> handlePacksDetailsError(it)
            }
            showLoadingForGettingPacks(false)
        })
    }

    private fun observeCreateGameEvent() {
        newGameViewModel.getCreateGameEvent().observe(viewLifecycleOwner, EventObserver {
            if (!this.isAdded) return@EventObserver
            when (it) {
                is Resource.Success -> it.data?.let { session -> handleSucessfulGameCreation(session) }
                is Resource.Error -> handleErrorCreatingGame(it)
            }
        })
    }

    private fun triggerCreateGameEvent() {
        showLoadingForCreateGame(true)
        val timeLimit = tv_new_game_time.text.toString().trim()
        val playerName = tv_new_game_name.text.toString().trim()
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
        bundle.putParcelable(WaitingFragment.SESSION_KEY, currentSession)
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
        pb_new_game.goneIf(!loading)
        btn_create.text = if (loading) "" else getString(R.string.string_btn_create)
        btn_create.isClickable = !loading
        btn_packs.isClickable = !loading
    }

    private fun showLoadingForGettingPacks(loading: Boolean) {
        pb_packs.visibleIf(loading)
        btn_packs.visibleIf(!loading)
        btn_packs.isClickable = !loading
    }

    private fun configurePacksAdapter() {
        rv_packs.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = packsAdapter
            setHasFixedSize(true)
        }
    }

    private fun changeAccent() {
        btn_create.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_packs.setImageDrawable(drawable)

        UIHelper.updateDrawableToTheme(requireContext(), R.drawable.edit_text_custom_cursor)

        pb_packs.indeterminateDrawable
            .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN)

        pb_new_game.indeterminateDrawable
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }
}

