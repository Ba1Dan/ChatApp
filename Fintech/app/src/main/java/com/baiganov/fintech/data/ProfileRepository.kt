package com.baiganov.fintech.data

import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.network.NetworkModule
import io.reactivex.Single

class ProfileRepository {

    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}