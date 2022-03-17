package com.baiganov.fintech.di

import com.baiganov.fintech.data.datasource.ChannelsLocalDataSource
import com.baiganov.fintech.data.datasource.ChannelsRemoteDataSource
import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideUsersMapper(): UserToUserFingerPrintMapper {
        return UserToUserFingerPrintMapper()
    }

    @Provides
    @Singleton
    fun provideChannelsLocalDataSource(channelsLocalDataSource: ChannelsLocalDataSource.Base): ChannelsLocalDataSource {
        return channelsLocalDataSource
    }

    @Provides
    @Singleton
    fun provideChannelsRemoteDataSource(channelsRemoteDataSource: ChannelsRemoteDataSource.Base): ChannelsRemoteDataSource {
        return channelsRemoteDataSource
    }
}