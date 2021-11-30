package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.model.response.UsersResponse
import io.reactivex.Single

interface PeopleRepository {

    fun getUsers(): Single<UsersResponse>
}