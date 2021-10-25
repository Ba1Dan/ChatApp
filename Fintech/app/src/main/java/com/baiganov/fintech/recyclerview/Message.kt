package com.baiganov.fintech.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.User
import com.baiganov.fintech.model.Content

class Message(
    var content: Content
) : Item {
    override val id: String = content.id.toString()
    override val viewType: Int = if (content.userId == User.getId()) R.layout.outgoing_message else R.layout.incoming_message
}