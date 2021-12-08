package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.data.model.response.UsersResponse
import io.reactivex.Single
import javax.inject.Inject

class PeopleRemoteDataSource @Inject constructor(
    private val service: ChatApi,
) {

    fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}