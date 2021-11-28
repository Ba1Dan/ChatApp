package com.baiganov.fintech.presentation.ui.chat.recyclerview

import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

interface ItemClickListener {

    fun onItemClick(position: Int, item: ItemFingerPrint)

}