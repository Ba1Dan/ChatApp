package com.baiganov.fintech.ui.chat.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint


abstract class BaseViewHolder< I : ItemFingerPrint>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: I)
}