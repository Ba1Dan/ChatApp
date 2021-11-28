package com.baiganov.fintech.di

import com.baiganov.fintech.data.ChannelsRepositoryImpl
import com.baiganov.fintech.data.MessageRepositoryImpl
import com.baiganov.fintech.data.PeopleRepositoryImpl
import com.baiganov.fintech.data.ProfileRepositoryImpl
import com.baiganov.fintech.domain.repositories.ChannelsRepository
import com.baiganov.fintech.domain.repositories.MessageRepository
import com.baiganov.fintech.domain.repositories.PeopleRepository
import com.baiganov.fintech.domain.repositories.ProfileRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun bindPeopleRepository(impl: PeopleRepositoryImpl): PeopleRepository

    @Binds
    fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    fun bindChannelsRepository(impl: ChannelsRepositoryImpl): ChannelsRepository
}