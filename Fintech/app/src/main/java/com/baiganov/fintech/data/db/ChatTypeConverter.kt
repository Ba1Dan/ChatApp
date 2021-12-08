package com.baiganov.fintech.data.db

import androidx.room.TypeConverter
import com.baiganov.fintech.data.model.Topic
import com.baiganov.fintech.data.model.response.Reaction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatTypeConverter {

    @TypeConverter
    fun reactionsToString(value: List<Reaction>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun stringToReactions(value: String): List<Reaction> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun topicsToString(value: List<Topic>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
     fun stringToTopics(value: String): List<Topic> {
        return Json.decodeFromString(value)
    }
}