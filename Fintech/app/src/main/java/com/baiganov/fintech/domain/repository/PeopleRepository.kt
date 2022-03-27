package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.db.entity.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface PeopleRepository {

    fun loadUsers(): Completable

    fun searchUser(name: String): Flowable<List<UserEntity>>
}