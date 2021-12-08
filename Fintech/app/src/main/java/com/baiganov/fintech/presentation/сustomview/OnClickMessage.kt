package com.baiganov.fintech.presentation.сustomview

import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick

interface OnClickMessage {

    fun onItemClick(click: TypeClick)

    fun addReaction(messageId: Int, emojiName: String, position: Int)

    fun deleteReaction(messageId: Int, emojiName: String, position: Int)
}