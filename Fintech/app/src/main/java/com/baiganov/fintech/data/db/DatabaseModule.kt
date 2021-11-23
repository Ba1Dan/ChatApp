package com.baiganov.fintech.data.db

import android.content.Context
import androidx.room.Room

class DatabaseModule() {

    fun create(context: Context): ChatDatabase =  Room.databaseBuilder(
        context,
        ChatDatabase::class.java,
        ChatDatabase.DATABASE_NAME
    ).build()
}