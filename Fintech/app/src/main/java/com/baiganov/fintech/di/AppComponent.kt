package com.baiganov.fintech.di

import android.content.Context
import com.baiganov.fintech.presentation.ui.MainActivity
import com.baiganov.fintech.presentation.ui.channels.ChannelsFragment
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsFragment
import com.baiganov.fintech.presentation.ui.chat.ChatActivity
import com.baiganov.fintech.presentation.ui.people.PeopleFragment
import com.baiganov.fintech.presentation.ui.profile.ProfileFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class, DomainModule::class, UriReaderModule::class, DataModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(channelsFragment: ChannelsFragment)
    fun inject(streamsFragment: StreamsFragment)
    fun inject(peopleFragment: PeopleFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(chatActivity: ChatActivity)

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}