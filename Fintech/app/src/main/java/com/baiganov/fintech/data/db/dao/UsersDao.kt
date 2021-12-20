package com.baiganov.fintech.data.db.dao

import androidx.room.*
import com.baiganov.fintech.data.db.entity.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUsers(users: List<UserEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: UserEntity): Completable

    @Query("SELECT * FROM users_table")
    fun getUsers(): Flowable<List<UserEntity>>
}