package com.baiganov.fintech.data

import com.baiganov.fintech.model.Profile
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ProfileRepository {

    private val dataManager = DataManager()

    fun loadProfile(): Observable<Profile> {
        return Observable.fromCallable { dataManager.profile }
            .delay(5000L, TimeUnit.MILLISECONDS)
    }
}