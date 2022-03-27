package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val service: ChatApi
) {

    fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}