package com.dangerfield.spyfall.newGame

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.dangerfield.spyfall.models.GamePack
import com.dangerfield.spyfall.util.BoldText
import kotlinx.android.synthetic.main.item_pack.view.*
import java.util.ArrayList


/**
 * Created by ELIJAH DANGERFIELD on 05/7/2019.
 */
class SimpleAdapter(var list: ArrayList<String>, private var context: Context?) : RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView

        init {
            textView = BoldText(context, null)
            context = view.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item_1, parent, false)
        return ViewHolder(singleButton)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]
        holder.textView.text = item

    }

    override fun getItemCount(): Int {
        return list.size
    }
}



