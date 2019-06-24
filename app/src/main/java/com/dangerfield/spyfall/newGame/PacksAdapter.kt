package com.dangerfield.spyfall.newGame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.dangerfield.spyfall.R
import com.dangerfield.spyfall.models.GamePack
import kotlinx.android.synthetic.main.item_pack.view.*
import java.util.ArrayList


/**
 * Created by ELIJAH DANGERFIELD on 05/7/2019.
 */
class PacksAdapter(var packs: ArrayList<GamePack>, private var context: Context?) : RecyclerView.Adapter<PacksAdapter.PackHolder>() {


    inner class PackHolder(view: View) : RecyclerView.ViewHolder(view) {
        var number: TextView
        var packType: TextView
        var background: ConstraintLayout

        init {
            number = view.findViewById(R.id.tv_pack_number)
            packType = view.findViewById(R.id.tv_pack_type)
            background = view.findViewById(R.id.card_background)

            context = view.context

            view.setOnClickListener {
                //TODO: play selection animaiton, and set set position's isSelected
                select(view,packs[adapterPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackHolder {
        val singleButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack, parent, false)
        return PackHolder(singleButton)
    }

    override fun onBindViewHolder(holder: PackHolder, position: Int) {

        val item = packs[position]
        holder.number.text = item.number.toString()
        holder.packType.text = item.type
        holder.background.setBackgroundColor(item.color)

    }

    override fun getItemCount(): Int {
        return packs.size
    }

    private fun select(view: View, pack: GamePack){
        if(pack.isSelected){
            //unselect
            pack.isSelected = false
            view.view_pack_filter.visibility = View.INVISIBLE
            view.check_animation.visibility = View.INVISIBLE
        }

        else{
            //select
            pack.isSelected = true
            view.view_pack_filter.visibility = View.VISIBLE
            view.check_animation.visibility = View.VISIBLE
            view.check_animation.playAnimation()
        }
    }
}