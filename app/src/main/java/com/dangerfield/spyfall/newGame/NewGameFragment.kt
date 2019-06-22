package com.dangerfield.spyfall.newGame


import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dangerfield.spyfall.util.UIHelper
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.game.GameViewModel
import com.dangerfield.spyfall.models.Game
import com.dangerfield.spyfall.models.GamePack
import kotlinx.android.synthetic.main.fragment_new_game.*
import java.util.*
import kotlin.collections.ArrayList

class NewGameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var packsAdapter: PacksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_game, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //assigned in on create, as this does not need to be assigned again
        viewModel = ViewModelProviders.of(activity!!).get(GameViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeAccent()

        //reference views once the view has been created
        tv_new_game_name.onFocusChangeListener = UIHelper.keyboardHider
        tv_new_game_time.onFocusChangeListener = UIHelper.keyboardHider

        btn_create.setOnClickListener { createGame() }

        btn_packs.setOnClickListener{
            showPacksDialog()
        }

        configurePacksAdapter()


    }

    private fun configurePacksAdapter(){

        var packs = mutableListOf<GamePack>()

        //TODO: make this dynamic by pulling pack names from firebase

        packs.add(GamePack(resources.getColor(R.color.colorPink),"Standard",1,"pack 1",false))
        packs.add(GamePack(resources.getColor(R.color.colorGreen),"Standard",2,"pack 2",false))
        packs.add(GamePack(resources.getColor(R.color.colorYellow),"Special",1,"special pack",false))

        rv_packs.apply{
            layoutManager = GridLayoutManager(context, 3)
            packsAdapter = PacksAdapter(packs as ArrayList<GamePack>,context!!)
            adapter = packsAdapter
            setHasFixedSize(true)
        }
    }

    private fun createGame(){

        val timeLimit = tv_new_game_time.text.toString().trim()
        val playerName = tv_new_game_name.text.toString().trim()
        //these strings will be used for queries of the firestore database for which locations to include
        var chosenPacks = packsAdapter.packs.filter {it.isSelected}.map { it.queryString } as ArrayList<String>



        when {
            chosenPacks.isEmpty() -> {Toast.makeText(context,"Please select a pack", Toast.LENGTH_LONG).show()
                return}

            playerName.isEmpty() -> {Toast.makeText(context, "please enter a name", Toast.LENGTH_LONG).show()
            return}

            playerName.length > 25 -> {Toast.makeText(context, "please enter a name less than 25 characters", Toast.LENGTH_LONG).show()
                return}

            timeLimit.isEmpty() || timeLimit.toInt() > 10 -> {
                Toast.makeText(context, "please enter a time limit less than 10 minutes", Toast.LENGTH_LONG).show()
                return
            }
        }

        viewModel.currentUser = playerName
        createGame(Game("",chosenPacks,false,
            mutableListOf(playerName) as ArrayList, ArrayList(),timeLimit.toLong()))
    }

    private fun createGame(game: Game){

        val navController = NavHostFragment.findNavController(this)

        if(viewModel.hasNetworkConnection) {

            //TODO: consider timeout function here
            loadMode()

            viewModel.createGame(game, UUID.randomUUID().toString().substring(0, 6).toLowerCase())
                .addOnCompleteListener {
                    enterMode()
                    val bundle = bundleOf("FromFragment" to "NewGameFragment")
                    navController.navigate(R.id.action_newGameFragment_to_waitingFragment, bundle)
                }
        }else{
            UIHelper.customSimpleAlert(context!!, "Something went wrong",
                "We are sorry. Please check your internet connection and try again",
                "Okay",{},"",{}).show()
        }

    }

    fun loadMode(){
        pb_new_game.visibility = View.VISIBLE
        btn_create.isClickable = false
        btn_packs.isClickable = false

    }
    fun enterMode(){
        pb_new_game.visibility = View.INVISIBLE
        btn_create.isClickable = true
        btn_packs.isClickable = true

    }

    private fun changeAccent(){
        btn_create.background.setTint(UIHelper.accentColor)

        val drawable = resources.getDrawable(R.drawable.ic_rules).mutate()
        drawable.setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_ATOP)
        btn_packs.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        btn_packs.setTextColor(UIHelper.accentColor)

        UIHelper.setCursorColor(tv_new_game_name,UIHelper.accentColor)

        pb_new_game.indeterminateDrawable
            .setColorFilter(UIHelper.accentColor, PorterDuff.Mode.SRC_IN )

    }

    fun showPacksDialog(){

        //we also might consider a different structure for the backend where the packs are kept in on collection
        //TODO: you would want to pass in the entire list to the UI helper and have it dynamically create a recycler view for every indecy
        loadMode()
        val list = mutableListOf<List<String>>()
        viewModel.getPackNames().addOnSuccessListener {
            val packNames=  it.documents.map {document -> document.id }
            var completedTasks = 0
            for (i in 0 until packNames.size) {

                viewModel.db.collection(packNames[i]).get().addOnSuccessListener { pack ->
                    list.add(pack.documents.map {location -> location.id } )

                }.addOnCompleteListener {
                    completedTasks += 1
                    if(completedTasks == packNames.size){
                        //then youre done
                        enterMode()
                        UIHelper.packsDialog(context!!,list).show()

                    }
                }
            }


        }
    }
}
