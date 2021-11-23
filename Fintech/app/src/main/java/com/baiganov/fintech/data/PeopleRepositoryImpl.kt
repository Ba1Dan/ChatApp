package com.baiganov.fintech.data

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.domain.repositories.PeopleRepository
import com.baiganov.fintech.model.response.UsersResponse
import io.reactivex.Single

class PeopleRepositoryImpl(
    private val service: ChatApi,
) : PeopleRepository {

    override fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}