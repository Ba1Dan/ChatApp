package com.baiganov.fintech.domain.usecase.profile

import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.domain.repository.ProfileRepository
import io.reactivex.Single
import javax.inject.Inject

class LoadProfileUseCase @Inject constructor(private val repository: ProfileRepository) {

    fun execute(): Single<User> {
        return repository.loadProfile()
    }
}