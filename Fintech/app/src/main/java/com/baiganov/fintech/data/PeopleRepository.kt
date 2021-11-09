package com.baiganov.fintech.data

import com.baiganov.fintech.model.response.UsersResponse
import com.baiganov.fintech.network.NetworkModule
import com.baiganov.fintech.ui.people.adapters.UserFingerPrint
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit


class PeopleRepository {

    private val dataManager = DataManager()
    private val networkModule = NetworkModule()
    private val service = networkModule.create()

    fun loadUsers(): Observable<List<UserFingerPrint>> {
        return Observable.fromCallable { dataManager.users }
            .delay(5000L, TimeUnit.MILLISECONDS)

    }

    fun getUsers(): Single<UsersResponse> {
        return service.getUsers()
    }
}