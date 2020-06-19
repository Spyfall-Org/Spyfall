package com.dangerfield.spyfall.newGame

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.crashlytics.android.Crashlytics
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.api.Resource
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.util.Connectivity
import com.dangerfield.spyfall.util.addCharacterMax
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_new_game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class NewGameFragment : Fragment(R.layout.fragment_new_game) {

    private val newGameViewModel: NewGameViewModel by viewModel()
    private val packsAdapter: PacksAdapter by lazy {PacksAdapter( getPacks(), requireContext())}
    private val navController by lazy { NavHostFragment.findNavController(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    //TODO make this dynamic via firebase call
    private fun getPacks()
        = arrayListOf(GamePack(UIHelper.accentColors[0],"Standard",1,"Standard Pack 1",false),
            GamePack(UIHelper.accentColors[1],"Standard",2,"Standard Pack 2",false),
            GamePack(UIHelper.accentColors[2],"Special",1,"Special Pack 1",false))

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
                is Resource.Success -> handleSucessfulGameCreation()
                is Resource.Error -> it.error?.let { e -> handleErrorCreatingGame(e) }
            }
        })
    }

    private fun handleErrorCreatingGame(error: NewGameError) {
        if(error == NewGameError.NETWORK_ERROR) {
            UIHelper.errorDialog(requireContext()).show()
        } else {
            error.resId?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
            }
        }
        enterMode()
    }

    private fun handleSucessfulGameCreation() {
        if(navController.currentDestination?.id == R.id.newGameFragment) {
            enterMode()
            navController.navigate(R.id.action_newGameFragment_to_waitingFragment)
        }
    }

    private fun getPacksInfo() {
        loadModePacksInfo()
        newGameViewModel.getPacksDetails().observe(viewLifecycleOwner, Observer {
            if(!this.isAdded) return@Observer
            when(it) {
                is Resource.Success -> it.data?.let{ d -> handlePacksDetailsSuccess(d) }
                is Resource.Error -> it.error?.let{ e -> handlePacksDetailsError(e) }
            }
        })
    }

    private fun handlePacksDetailsError(error: PackDetailsError) {
        if(error == PackDetailsError.NETWORK_ERROR) {
            UIHelper.errorDialog(requireContext()).show()
        } else {
            error.resId?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
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

