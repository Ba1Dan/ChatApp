package com.baiganov.fintech.сustomview

import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

interface OnClickMessage {

    fun onItemClick(position: Int, item: ItemFingerPrint)

    fun addReaction(idMessage: Int, nameEmoji: String, position: Int)

    fun deleteReaction(idMessage: Int, nameEmoji: String, position: Int)
}