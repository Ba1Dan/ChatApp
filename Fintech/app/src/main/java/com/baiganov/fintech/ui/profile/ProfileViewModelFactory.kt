package com.baiganov.fintech.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.domain.repositories.ProfileRepository

class ProfileViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}