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
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.models.Session
import com.dangerfield.spyfall.util.addCharacterMax
import com.dangerfield.spyfall.ui.waiting.WaitingFragment
import com.dangerfield.spyfall.util.LogHelper
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
        tv_new_game_name.onFocusChangeListener = UIHelper.keyboardHider
        tv_new_game_time.onFocusChangeListener = UIHelper.keyboardHider
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

    private fun createGame(){
        loadMode()
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

        enterMode()
    }

    private fun handleSucessfulGameCreation(currentSession: Session) {
        val bundle = Bundle()
        bundle.putParcelable(WaitingFragment.SESSION_KEY, currentSession)
        if(navController.currentDestination?.id == R.id.newGameFragment) {
            enterMode()
            navController.navigate(R.id.action_newGameFragment_to_waitingFragment, bundle)
        }
    }

    private fun getPacksInfo() {
        loadModePacksInfo()
        newGameViewModel.getPacksDetails().observe(viewLifecycleOwner, Observer {
            if(!this.isAdded) return@Observer
            when(it) {
                is Resource.Success -> it.data?.let{ d -> handlePacksDetailsSuccess(d) }
                is Resource.Error -> handlePacksDetailsError(it)
            }
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
        enterModePacksInfo()
    }


    private fun handlePacksDetailsSuccess(data: List<List<String>>) {
        UIHelper.packsDialog(requireContext(), data as MutableList<List<String>>).show()
        enterModePacksInfo()
    }

    private fun loadMode(){
        pb_new_game.visibility = View.VISIBLE
        btn_create.text = ""
        btn_create.isClickable = false
        btn_packs.isClickable = false
    }
    private fun enterMode(){
        pb_new_game.visibility = View.INVISIBLE
        btn_create.text = getString(R.string.string_btn_create)
        btn_create.isClickable = true
        btn_packs.isClickable = true
    }

    private fun loadModePacksInfo() {
        pb_packs.visibility = View.VISIBLE
        btn_packs.visibility = View.INVISIBLE
        btn_packs.isClickable = false
    }

    private fun enterModePacksInfo() {
        pb_packs.visibility = View.INVISIBLE
        btn_packs.visibility = View.VISIBLE
        btn_packs.isClickable = true
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
