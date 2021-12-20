package com.baiganov.fintech.presentation.model

import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.UserEntity

class UserFingerPrint(
    val user: UserEntity
) : ItemFingerPrint {
    override val viewType: Int =  R.layout.item_user
    override val id: Int = user.userId
}