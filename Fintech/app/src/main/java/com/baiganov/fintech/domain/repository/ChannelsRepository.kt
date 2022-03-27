package com.baiganov.fintech.domain.repository

import com.baiganov.fintech.data.db.entity.StreamEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


interface ChannelsRepository {

    fun getSubscribedStreams(): Completable

    fun getAllStreams(): Completable

    fun searchSubscribedStreams(searchQuery: String): Flowable<List<StreamEntity>>

    fun searchAllStreams(searchQuery: String): Flowable<List<StreamEntity>>

    fun createStream(name: String, description: String): Completable
}