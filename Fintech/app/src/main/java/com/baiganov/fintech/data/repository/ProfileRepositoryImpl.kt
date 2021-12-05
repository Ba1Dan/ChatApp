package com.baiganov.fintech.data.repository

import com.baiganov.fintech.data.datasource.ProfileRemoteDataSource
import com.baiganov.fintech.domain.repository.ProfileRepository
import com.baiganov.fintech.data.model.response.User
import io.reactivex.Single
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    override fun loadProfile(): Single<User> {
        return profileRemoteDataSource.loadProfile()
    }
}