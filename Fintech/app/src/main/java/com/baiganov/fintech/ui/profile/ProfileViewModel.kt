package com.baiganov.fintech.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repositories.ProfileRepository
import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.ui.Event
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _profile: MutableLiveData<State<User>> = MutableLiveData()
    val profile: LiveData<State<User>>
        get() = _profile

    fun obtainEvent(event: Event.EventProfile) {
        when (event) {
            is Event.EventProfile.LoadProfile -> {
                loadProfile()
            }
        }
    }

    fun loadProfile() {
        profileRepository.loadProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _profile.postValue(State.Loading()) }
            .subscribeBy(
                onSuccess = { _profile.value = State.Result(it) },
                onError = {
                    _profile.value = State.Error(message = it.message)
                }
            )
            .addTo(compositeDisposable)
    }
}