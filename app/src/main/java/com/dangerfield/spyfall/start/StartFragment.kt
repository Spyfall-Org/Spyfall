package com.dangerfield.spyfall.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dangerfield.spyfall.R
import kotlinx.android.synthetic.main.start_fragment.*


class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.start_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_new_game.setOnClickListener {
            var debug = fragmentManager
            Navigation.findNavController(it).navigate(R.id.action_startFragment_to_newGameFragment)
        }

        btn_join_game.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_startFragment_to_joinGameFragment)
        }
    }

}
