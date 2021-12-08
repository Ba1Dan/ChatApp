package com.baiganov.fintech.presentation.model

import com.baiganov.fintech.R
import com.baiganov.fintech.data.model.response.User

class UserFingerPrint(
    val user: User
) : ItemFingerPrint {
    override val viewType: Int =  R.layout.item_user
    override val id: Int = user.userId
}