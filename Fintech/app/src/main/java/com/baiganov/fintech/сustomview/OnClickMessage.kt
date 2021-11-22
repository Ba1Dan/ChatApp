package com.baiganov.fintech.сustomview

import com.baiganov.fintech.ui.chat.bottomsheet.TypeClick

interface OnClickMessage {

    fun onItemClick(click: TypeClick)

    fun addReaction(messageId: Int, emojiName: String, position: Int)

    fun deleteReaction(messageId: Int, emojiName: String, position: Int)
}