package com.baiganov.fintech.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.data.StreamRepository

class ChannelsViewModelFactory(private val streamRepository: StreamRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelsViewModel::class.java)) {
            return ChannelsViewModel(streamRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}