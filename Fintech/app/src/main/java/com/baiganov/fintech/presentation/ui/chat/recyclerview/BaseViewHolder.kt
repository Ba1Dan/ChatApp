package com.baiganov.fintech.presentation.ui.chat.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.presentation.model.ItemFingerPrint


abstract class BaseViewHolder< I : ItemFingerPrint>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: I)
}