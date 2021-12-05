package com.baiganov.fintech.presentation.ui.people.adapters

import com.baiganov.fintech.model.response.UsersResponse

class UserToUserFingerPrintMapper : (UsersResponse) -> (List<UserFingerPrint>) {

    override fun invoke(response: UsersResponse): List<UserFingerPrint> {
        return response.users.map { user ->
            UserFingerPrint(user)
        }
    }
}