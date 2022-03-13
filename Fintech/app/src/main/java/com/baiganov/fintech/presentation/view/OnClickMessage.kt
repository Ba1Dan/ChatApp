package com.baiganov.fintech.presentation.view

import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick

interface OnClickMessage {

    fun onItemClick(click: TypeClick)

    fun addReaction(messageId: Int, emojiName: String)

    fun deleteReaction(messageId: Int, emojiName: String)
}