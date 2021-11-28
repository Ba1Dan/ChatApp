package com.baiganov.fintech.data

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.domain.repositories.PeopleRepository
import com.baiganov.fintech.model.response.UsersResponse
import io.reactivex.Single
import javax.inject.Inject

class PeopleRepositoryImpl @Inject constructor(
    private val service: ChatApi,
) : PeopleRepository {

    override fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}