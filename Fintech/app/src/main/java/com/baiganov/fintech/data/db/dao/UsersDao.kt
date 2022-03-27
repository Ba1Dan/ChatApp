package com.baiganov.fintech.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baiganov.fintech.data.db.entity.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUsers(users: List<UserEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: UserEntity): Completable

    @Query("SELECT * FROM users_table WHERE full_name LIKE :name")
    fun searchUsers(name: String): Flowable<List<UserEntity>>
}