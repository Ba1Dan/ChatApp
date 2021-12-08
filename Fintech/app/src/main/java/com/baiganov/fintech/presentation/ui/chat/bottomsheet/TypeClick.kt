package com.baiganov.fintech.presentation.ui.chat.bottomsheet

import com.baiganov.fintech.data.db.entity.MessageEntity

sealed interface TypeClick {

    class AddReaction(val messageId: Int?, val emoji: String) : TypeClick
    class EditMessage(val message: MessageEntity) : TypeClick
    class DeleteMessage(val messageId: Int) : TypeClick

    class OpenBottomSheet(val messageId: Int) : TypeClick
    class OpenActionDialog(val message: MessageEntity) : TypeClick
}
