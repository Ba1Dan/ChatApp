package com.baiganov.fintech.ui.chat.bottomsheet

sealed class TypeActionMessage {

    data class AddReaction(val messageId: Int?, val emoji: String) : TypeActionMessage()
    data class EditMessage(val messageId: Int?) : TypeActionMessage()
    data class DeleteMessage(val messageId: Int?) : TypeActionMessage()
}
