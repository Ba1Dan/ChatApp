package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.model.response.User
import io.reactivex.Single

interface ProfileRepository {

    fun loadProfile(): Single<User>
}