package com.baiganov.fintech.data.datasource

import com.baiganov.fintech.data.db.dao.UsersDao
import com.baiganov.fintech.data.db.entity.UserEntity
import javax.inject.Inject

class PeopleLocalDataSource @Inject constructor(
    private val usersDao: UsersDao
) {

    fun saveUsers(users: List<UserEntity>) = usersDao.saveUsers(users)

    fun searchUsers(name: String) = usersDao.searchUsers(name)
}