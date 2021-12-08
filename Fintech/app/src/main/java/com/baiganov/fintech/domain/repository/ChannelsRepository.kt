package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.Completable
import io.reactivex.Flowable

interface ChannelsRepository {

    fun getStreams(type: Int): Completable

    fun searchStreams(searchQuery: String, type: Int?): Flowable<List<StreamEntity>>

    fun createStream(name: String, description: String): Completable
}