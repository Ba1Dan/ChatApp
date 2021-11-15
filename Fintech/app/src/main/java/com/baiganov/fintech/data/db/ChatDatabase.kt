package com.baiganov.fintech.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baiganov.fintech.data.db.entity.StreamEntity

@Database(entities = [StreamEntity::class], version = 1, exportSchema = false)
@TypeConverters(ChatTypeConverter::class)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun streamsDao(): StreamsDao
    abstract fun messagesDao(): MessagesDao

    companion object {
        const val DATABASE_NAME = "chat_database"
    }
}