package com.baiganov.fintech.di

import androidx.lifecycle.ViewModel
import com.baiganov.fintech.presentation.ui.channels.ChannelsViewModel
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsViewModel
import com.baiganov.fintech.presentation.ui.chat.ChatViewModel
import com.baiganov.fintech.presentation.ui.people.PeopleViewModel
import com.baiganov.fintech.presentation.ui.profile.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    fun bindChatViewModel(viewModel: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StreamsViewModel::class)
    fun bindStreamsViewModel(viewModel: StreamsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChannelsViewModel::class)
    fun bindChannelsViewModel(viewModel: ChannelsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PeopleViewModel::class)
    fun bindPeopleViewModel(viewModel: PeopleViewModel): ViewModel
}