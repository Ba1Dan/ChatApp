package com.baiganov.fintech.domain.repositories

import com.baiganov.fintech.model.response.User
import io.reactivex.Single

interface ProfileRepository {

    fun loadProfile(): Single<User>
}