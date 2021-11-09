package com.baiganov.fintech.data

import com.baiganov.fintech.ui.people.adapters.UserFingerPrint
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class PeopleRepository {

    private val dataManager = DataManager()

    fun loadUsers(): Observable<List<UserFingerPrint>> {
        return Observable.fromCallable { dataManager.users }
            .delay(5000L, TimeUnit.MILLISECONDS)

    }
}