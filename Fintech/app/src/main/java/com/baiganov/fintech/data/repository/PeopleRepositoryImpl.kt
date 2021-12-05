package com.baiganov.fintech.data.repository

import com.baiganov.fintech.data.datasource.PeopleRemoteDataSource
import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.data.model.response.UsersResponse
import io.reactivex.Single
import javax.inject.Inject

class PeopleRepositoryImpl @Inject constructor(
    private val peopleRemoteDataSource: PeopleRemoteDataSource,
) : PeopleRepository {

    override fun getUsers(): Single<UsersResponse> {
        return peopleRemoteDataSource.getUsers()
    }
}