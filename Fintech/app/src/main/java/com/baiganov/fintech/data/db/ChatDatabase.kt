package com.baiganov.fintech.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baiganov.fintech.data.db.dao.StreamsDao
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.db.dao.MessagesDao
import com.baiganov.fintech.data.db.dao.UsersDao
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.data.db.entity.UserEntity

@Database(entities = [StreamEntity::class, MessageEntity::class, UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(ChatTypeConverter::class)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun streamsDao(): StreamsDao
    abstract fun messagesDao(): MessagesDao
    abstract fun usersDao(): UsersDao

    companion object {
        const val DATABASE_NAME = "chat_database"
    }
}