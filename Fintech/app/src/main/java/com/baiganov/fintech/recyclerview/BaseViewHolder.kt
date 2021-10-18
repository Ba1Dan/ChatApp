package com.baiganov.fintech.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.model.Item

abstract class BaseViewHolder<out V : View, in I : Item>(
    val view: V
) : RecyclerView.ViewHolder(view) {

    abstract fun onBind(item: I)
}