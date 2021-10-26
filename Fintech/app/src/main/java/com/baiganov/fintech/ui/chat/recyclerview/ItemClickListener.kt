package com.baiganov.fintech.ui.chat.recyclerview

import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

interface ItemClickListener {

    fun onItemClick(position: Int, item: ItemFingerPrint)
}