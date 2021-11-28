package com.baiganov.fintech.presentation.ui.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repositories.PeopleRepository
import com.baiganov.fintech.presentation.util.Event
import com.baiganov.fintech.presentation.ui.people.adapters.UserFingerPrint
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PeopleViewModel @Inject constructor(
    private val peopleRepository: PeopleRepository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _users: MutableLiveData<State<List<UserFingerPrint>>> = MutableLiveData()
    val users: LiveData<State<List<UserFingerPrint>>>
        get() = _users

    fun obtainEvent(event: Event.EventPeople) {
        when (event) {
            is Event.EventPeople.LoadUsers -> {
                getUsers()
            }
        }
    }

    private fun getUsers() {
        peopleRepository.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _users.postValue(State.Loading()) }
            .subscribeBy(
                onSuccess = {
                    val users = it.users.map { user ->
                        UserFingerPrint(user)
                    }

                    _users.value = State.Result(users)
                },
                onError = { _users.value = State.Error(it.message) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}