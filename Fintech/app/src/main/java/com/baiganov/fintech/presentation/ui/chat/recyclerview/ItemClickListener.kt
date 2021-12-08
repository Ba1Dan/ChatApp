package com.baiganov.fintech.presentation.ui.chat.recyclerview

import com.baiganov.fintech.presentation.model.ItemFingerPrint

interface ItemClickListener {

    fun onItemClick(position: Int, item: ItemFingerPrint)

}