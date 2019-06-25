package com.dangerfield.spyfall.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dangerfield.spyfall.R


/**
 * Created by ELIJAH DANGERFIELD on 05/7/2019.
 */
class SimpleTextAdapter(var list: List<String>, private var context: Context?)
    : RecyclerView.Adapter<SimpleTextAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.tv_simple_text)

        init { context = view.context }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleButton = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_text, parent, false)
        return ViewHolder(singleButton)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = list[position]
    }

    override fun getItemCount(): Int { return list.size }
}



