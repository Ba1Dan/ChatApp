package com.baiganov.fintech.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baiganov.fintech.data.db.entity.UserEntity.Companion.USER_TABLE

@Entity(tableName = USER_TABLE)
data class UserEntity(
    @ColumnInfo(name = "user_id") @PrimaryKey val userId: Int,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "is_bot") val isBot: Boolean,
    @ColumnInfo(name = "status") val status: String,
) {

    companion object {
        const val USER_TABLE = "users_table"
    }
}