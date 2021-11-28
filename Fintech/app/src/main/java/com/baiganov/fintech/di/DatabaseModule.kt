package com.baiganov.fintech.di

import android.content.Context
import androidx.room.Room
import com.baiganov.fintech.data.db.ChatDatabase
import com.baiganov.fintech.data.db.MessagesDao
import com.baiganov.fintech.data.db.StreamsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(context: Context): ChatDatabase =  Room.databaseBuilder(
        context,
        ChatDatabase::class.java,
        ChatDatabase.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideStreamsDao(db: ChatDatabase): StreamsDao {
        return db.streamsDao()
    }

    @Singleton
    @Provides
    fun provideMessagesDao(db: ChatDatabase): MessagesDao {
        return db.messagesDao()
    }
}