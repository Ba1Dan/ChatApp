package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.model.response.User
import io.reactivex.Single
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val service: ChatApi
) {

    fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}