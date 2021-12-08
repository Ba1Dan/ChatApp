package com.baiganov.fintech.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baiganov.fintech.data.db.entity.StreamEntity.Companion.STREAMS_TABLE
import com.baiganov.fintech.data.model.Topic

@Entity(tableName = STREAMS_TABLE)
data class StreamEntity(
    @ColumnInfo(name = "stream_id") @PrimaryKey val streamId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "topics") val topics: List<Topic>,
    @ColumnInfo(name = "is_subscribed") val isSubscribed: Boolean
) {

    companion object {
        const val STREAMS_TABLE = "streams_table"
    }
}