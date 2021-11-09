package com.baiganov.fintech.ui.chat.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.MyUser
import com.baiganov.fintech.model.response.Message
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class MessageFingerPrint(
    var message: Message
) : ItemFingerPrint {
    override val id: String = message.id.toString()
    override val viewType: Int = if (message.senderId == MyUser.getId()) R.layout.outgoing_message else R.layout.incoming_message
}