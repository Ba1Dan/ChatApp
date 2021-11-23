package com.baiganov.fintech.data

import com.baiganov.fintech.data.network.ChatApi
import com.baiganov.fintech.domain.repositories.ProfileRepository
import com.baiganov.fintech.model.response.User
import io.reactivex.Single

class ProfileRepositoryImpl(
    private val service: ChatApi,
) : ProfileRepository {

    override fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}