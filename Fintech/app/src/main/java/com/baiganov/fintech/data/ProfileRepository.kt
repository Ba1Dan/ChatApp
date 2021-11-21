package com.baiganov.fintech.data

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.model.response.User
import io.reactivex.Single

class ProfileRepository(
    private val service: ChatApi,
) {

    fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}