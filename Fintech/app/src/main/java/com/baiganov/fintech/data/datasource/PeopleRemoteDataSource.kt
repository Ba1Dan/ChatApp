package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.model.UserPresenceResponse
import com.baiganov.fintech.data.model.UsersResponse
import com.baiganov.fintech.data.network.ChatApi
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PeopleRemoteDataSource @Inject constructor(
    private val service: ChatApi,
) {

    fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }

    fun getUserPresence(userId: String): Single<UserPresenceResponse> {
        return service.getUserPresence(userId)
    }
}