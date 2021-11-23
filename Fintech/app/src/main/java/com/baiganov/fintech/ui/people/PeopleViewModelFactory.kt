package com.baiganov.fintech.ui.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.domain.repositories.PeopleRepository

class PeopleViewModelFactory(private val peopleRepository: PeopleRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
            return PeopleViewModel(peopleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}