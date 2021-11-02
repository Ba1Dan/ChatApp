package com.baiganov.fintech.ui.chat.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.User
import com.baiganov.fintech.model.Content
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class MessageFingerPrint(
    var content: Content
) : ItemFingerPrint {
    override val id: String = content.id.toString()
    override val viewType: Int = if (content.userId == User.getId()) R.layout.outgoing_message else R.layout.incoming_message
}