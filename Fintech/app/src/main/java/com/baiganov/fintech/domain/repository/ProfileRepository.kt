package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.model.User
import io.reactivex.rxjava3.core.Single

interface ProfileRepository {

    fun loadProfile(): Single<User>
}