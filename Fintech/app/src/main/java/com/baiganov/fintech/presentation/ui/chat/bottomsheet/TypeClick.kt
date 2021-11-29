package com.baiganov.fintech.presentation.ui.chat.bottomsheet

sealed interface TypeClick {

    class AddReaction(val messageId: Int?, val emoji: String) : TypeClick
    class EditMessage(val messageId: Int?) : TypeClick
    class DeleteMessage(val messageId: Int) : TypeClick

    class OpenBottomSheet(val messageId: Int) : TypeClick
    class OpenActionDialog(val messageId: Int) : TypeClick
}
