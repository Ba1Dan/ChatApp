package com.baiganov.fintech.ui.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.data.PeopleRepository
import com.baiganov.fintech.ui.MainScreenState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class PeopleViewModel : ViewModel() {

    private val peopleRepository = PeopleRepository()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _users: MutableLiveData<MainScreenState> = MutableLiveData()
    val users: LiveData<MainScreenState>
        get() = _users

    fun loadUsers() {
        peopleRepository.loadUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _users.postValue(MainScreenState.Loading) }
            .subscribeBy(
                onNext = { _users.value = MainScreenState.Result(it) },
                onError = { _users.value = MainScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}