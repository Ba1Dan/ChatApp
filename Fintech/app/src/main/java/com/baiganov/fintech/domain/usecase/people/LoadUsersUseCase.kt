package com.baiganov.fintech.domain.usecase.people

import com.baiganov.fintech.domain.repository.PeopleRepository
import io.reactivex.Completable
import javax.inject.Inject

class LoadUsersUseCase @Inject constructor(private val repository: PeopleRepository) {

    fun execute(): Completable {
        return repository.loadUsers()
    }
}