package com.baiganov.fintech.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baiganov.fintech.data.db.entity.MessageEntity.Companion.MESSAGES_TABLE
import com.baiganov.fintech.data.model.Reaction
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = MESSAGES_TABLE)
data class MessageEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: Int,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "reactions") var reactions: @RawValue MutableList<Reaction>,
    @ColumnInfo(name = "sender_email") val senderEmail: String,
    @ColumnInfo(name = "sender_full_name") val senderFullName: String,
    @ColumnInfo(name = "sender_id") val senderId: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "stream_id") val streamId: Int,
    @ColumnInfo(name = "topic_name") val topicName: String
) : Parcelable {


    companion object {
        const val MESSAGES_TABLE = "messages_table"
    }
}