package com.baiganov.fintech.data

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.model.response.UsersResponse
import io.reactivex.Single

class PeopleRepository(
    private val service: ChatApi,
) {

    fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}