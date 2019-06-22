package com.dangerfield.spyfall.util

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.TypedValue
import com.dangerfield.spyfall.R


class BoldText(context: Context?, text: String?) : TextView(context) {


    init {
        this.text = text
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.boldTextDimen))
        setTypeface(typeface,Typeface.BOLD)
        id = View.generateViewId()
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}