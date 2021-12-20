package com.baiganov.fintech.presentation.model

import com.baiganov.fintech.data.db.entity.UserEntity

class UserToUserFingerPrintMapper : (List<UserEntity>) -> (List<UserFingerPrint>) {

    override fun invoke(users: List<UserEntity>): List<UserFingerPrint> {
        return users.map { user ->
            UserFingerPrint(user)
        }
    }
}