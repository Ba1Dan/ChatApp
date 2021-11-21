package com.baiganov.fintech.сustomview

import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

interface OnClickMessage {

    fun onItemClick(position: Int, item: ItemFingerPrint)

    fun addReaction(messageId: Int, emojiName: String, position: Int)

    fun deleteReaction(messageId: Int, emojiName: String, position: Int)
}