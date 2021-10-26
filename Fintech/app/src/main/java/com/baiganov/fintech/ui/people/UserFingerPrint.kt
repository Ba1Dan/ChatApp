package com.baiganov.fintech.ui.people

import com.baiganov.fintech.R
import com.baiganov.fintech.model.User
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class UserFingerPrint(
    val user: User
) : ItemFingerPrint {
    override val viewType: Int =  R.layout.item_user
    override val id: String = user.id.toString()
}