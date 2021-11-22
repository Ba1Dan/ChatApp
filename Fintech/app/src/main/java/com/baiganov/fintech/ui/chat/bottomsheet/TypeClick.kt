package com.baiganov.fintech.ui.chat.bottomsheet

sealed class TypeClick {

    data class AddReaction(val messageId: Int?, val emoji: String) : TypeClick()
    data class EditMessage(val messageId: Int?) : TypeClick()
    data class DeleteMessage(val messageId: Int?) : TypeClick()

    data class OpenBottomSheet(val messageId: Int) : TypeClick()
    data class OpenActionDialog(val messageId: Int) : TypeClick()
}
