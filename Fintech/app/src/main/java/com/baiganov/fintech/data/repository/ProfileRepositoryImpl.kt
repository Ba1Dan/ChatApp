package com.baiganov.fintech.data.repository

import com.baiganov.fintech.data.datasource.ProfileRemoteDataSource
import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.domain.repository.ProfileRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    override fun loadProfile(): Single<User> {
        return profileRemoteDataSource.loadProfile().subscribeOn(Schedulers.io())
    }
}