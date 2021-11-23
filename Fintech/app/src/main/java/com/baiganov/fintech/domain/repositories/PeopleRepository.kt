package com.baiganov.fintech.domain.repositories

import com.baiganov.fintech.model.response.UsersResponse
import io.reactivex.Single

interface PeopleRepository {

    fun getUsers(): Single<UsersResponse>
}