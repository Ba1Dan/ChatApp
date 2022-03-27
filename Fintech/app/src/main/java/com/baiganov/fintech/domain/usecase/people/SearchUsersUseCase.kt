package com.baiganov.fintech.domain.usecase.people

import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.presentation.model.UserFingerPrint
import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import io.reactivex.Observable
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: PeopleRepository,
    private val userToUserFingerPrintMapper: UserToUserFingerPrintMapper,
) {

    fun execute(searchQuery: String): Observable<List<UserFingerPrint>>? {
        return repository.searchUser(searchQuery).map(userToUserFingerPrintMapper).toObservable()
    }
}