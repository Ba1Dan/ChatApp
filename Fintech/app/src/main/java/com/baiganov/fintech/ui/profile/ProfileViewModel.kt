package com.baiganov.fintech.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.ProfileRepository
import com.baiganov.fintech.model.Profile
import com.baiganov.fintech.ui.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _profile: MutableLiveData<State<Profile>> = MutableLiveData()
    val profile: LiveData<State<Profile>>
        get() = _profile

    fun loadProfile() {
        profileRepository.loadProfile()
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _profile.postValue(State.Loading()) }
            .subscribeBy(
                onNext = { _profile.value = State.Result(it) },
                onError = { _profile.value = State.Error(message = it.message) }
            )
            .addTo(compositeDisposable)
    }
}