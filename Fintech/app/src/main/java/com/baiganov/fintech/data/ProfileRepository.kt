package com.baiganov.fintech.data

import com.baiganov.fintech.model.Profile
import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.network.NetworkModule
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class ProfileRepository {

    private val dataManager = DataManager()
    private val networkModule = NetworkModule()
    private val service = networkModule.create()

//    fun loadProfile(): Observable<Profile> {
//        return Observable.fromCallable { dataManager.profile }
//            .delay(5000L, TimeUnit.MILLISECONDS)
//    }

    fun loadProfile(): Single<User> {
        return service.getOwnUser()
    }
}