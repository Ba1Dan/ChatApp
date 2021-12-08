package com.baiganov.fintech.presentation.model

import com.baiganov.fintech.data.model.response.UsersResponse

class UserToUserFingerPrintMapper : (UsersResponse) -> (List<UserFingerPrint>) {

    override fun invoke(response: UsersResponse): List<UserFingerPrint> {
        return response.users.map { user ->
            UserFingerPrint(user)
        }
    }
}