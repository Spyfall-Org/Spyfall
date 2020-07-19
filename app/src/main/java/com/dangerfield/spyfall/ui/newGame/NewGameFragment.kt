package com.dangerfield.spyfall.ui.newGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.*
import kotlinx.android.synthetic.main.fragment_new_game.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class NewGameFragment : Fragment(R.layout.fragment_new_game) {

    private val newGameViewModel: NewGameViewModel by viewModel()
    private val packsAdapter: PacksAdapter by lazy {PacksAdapter(newGameViewModel.getPacks())}
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

    private fun setupView() {
        changeAccent()
        tv_new_game_name.setHideKeyBoardOnPressAway()
        tv_new_game_time.setHideKeyBoardOnPressAway()
        tv_new_game_time.addCharacterMax(2)
        tv_new_game_name.addCharacterMax(25)
        btn_create.setOnClickListener { createGame() }
        btn_packs.setOnClickListener{ getPacksInfo() }
        configurePacksAdapter()
    }

    private fun configurePacksAdapter(){
        rv_packs.apply{
            layoutManager = GridLayoutManager(context, 3)
            adapter = packsAdapter
            setHasFixedSize(true)
        }
    }

    //TODO change this to be a fire event
    private fun createGame(){
        showLoadingForCreateGame(true)
        val timeLimit = tv_new_game_time.text.toString().trim()
        val playerName = tv_new_game_name.text.toString().trim()
        val chosenPacks = packsAdapter.packs.filter {it.isSelected}.map { it.queryString } as ArrayList<String>

        newGameViewModel.createGame(playerName, timeLimit, chosenPacks).observe(viewLifecycleOwner, Observer {
            if(!this.isAdded) return@Observer
            when(it) {
                is Resource.Success -> it.data?.let{session -> handleSucessfulGameCreation(session) }
                is Resource.Error -> handleErrorCreatingGame(it)
            }
        })
    }

    private fun handleErrorCreatingGame(result: Resource.Error<Session, NewGameError>) {
        result.exception?.let { LogHelper.logErrorCreatingGame(it) }

        result.error?.let {error ->
            if(error == NewGameError.NETWORK_ERROR) {
                UIHelper.errorDialog(requireContext()).show()
            } else {
                error.resId?.let {
                    Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                }
            }
        }

        showLoadingForCreateGame(false)
    }

    private fun handleSucessfulGameCreation(currentSession: Session) {
        val bundle = Bundle()
        bundle.putParcelable(WaitingFragment.SESSION_KEY, currentSession)
        if(navController.currentDestination?.id == R.id.newGameFragment) {
            showLoadingForCreateGame(false)
            navController.navigate(R.id.action_newGameFragment_to_waitingFragment, bundle)
        }
    }

    //TODO change this to be a fire thingy, you can post the same value using a force
    private fun getPacksInfo() {
        showLoadingForGettingPacks(true)
        newGameViewModel.getPacksDetails().observe(viewLifecycleOwner, Observer {
            if(!this.isAdded) return@Observer
            when(it) {
                is Resource.Success -> it.data?.let{ d -> handlePacksDetailsSuccess(d) }
                is Resource.Error -> handlePacksDetailsError(it)
            }
            showLoadingForGettingPacks(false)
        })
    }

    private fun handlePacksDetailsError(result: Resource.Error<List<List<String>>, PackDetailsError>) {
        result.exception?.let { LogHelper.logErrorGettingPacksDetails(it) }

        result.error?.let {error ->
            if(error == PackDetailsError.NETWORK_ERROR) {
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
        btn_create.text = if(loading) "" else getString(R.string.string_btn_create)
        btn_create.isClickable = !loading
        btn_packs.isClickable = !loading
    }

    private fun showLoadingForGettingPacks(loading: Boolean) {
        pb_packs.goneIf(!loading)
        btn_packs.goneIf(loading)
        btn_packs.isClickable = !loading
    }

    private fun changeAccent(){
        btn_create.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_packs.setImageDrawable(drawable)

        UIHelper.setCursorColor(tv_new_game_name,UIHelper.accentColor)
        UIHelper.setCursorColor(tv_new_game_time,UIHelper.accentColor)

        pb_packs.indeterminateDrawable
            .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN )

        pb_new_game.indeterminateDrawable
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN )
    }
}

