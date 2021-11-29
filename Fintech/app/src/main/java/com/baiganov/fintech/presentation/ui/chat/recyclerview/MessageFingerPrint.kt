package com.baiganov.fintech.presentation.ui.chat.recyclerview

import com.baiganov.fintech.MyUser
import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class MessageFingerPrint(
    var message: MessageEntity
) : ItemFingerPrint {
    override val id: Int = message.id
    override val viewType: Int = if (message.senderId == MyUser.getId()) R.layout.outgoing_message else R.layout.incoming_message
}