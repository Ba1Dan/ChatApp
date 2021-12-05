package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.model.response.UsersResponse
import io.reactivex.Single

interface PeopleRepository {

    fun getUsers(): Single<UsersResponse>
}