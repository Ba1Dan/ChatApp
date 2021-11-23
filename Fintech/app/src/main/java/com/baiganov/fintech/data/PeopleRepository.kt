package com.baiganov.fintech.data

import com.baiganov.fintech.model.response.UsersResponse
import com.baiganov.fintech.data.network.NetworkModule
import io.reactivex.Single


class PeopleRepository {

    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}