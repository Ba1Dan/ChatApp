package com.baiganov.fintech.presentation.ui.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.model.UserFingerPrint
import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import com.baiganov.fintech.presentation.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PeopleViewModel @Inject constructor(
    private val repository: PeopleRepository,
    private val userToUserFingerPrintMapper: UserToUserFingerPrintMapper,
    private val networkManager: NetworkManager
) : ViewModel() {

    private val _state: MutableLiveData<State<List<UserFingerPrint>>> = MutableLiveData()
    val state: LiveData<State<List<UserFingerPrint>>>
        get() = _state

    private val compositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private var isLoading = true

    init {
        searchUsers()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun searchUsers(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    fun loadUsers() {
        repository.loadUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _state.value = State.Loading
            }
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                    isLoading = false
                },
                onError = { exception ->
                    _state.value = State.Error(exception.message)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun searchUsers() {
        searchSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery ->
                repository.searchUser(searchQuery)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map(userToUserFingerPrintMapper)
            .subscribeBy(
                onNext = {users ->
                    if (isLoading && users.isEmpty()) {
                        _state.value = State.Loading
                    } else {
                        _state.value = State.Result(users.sortedBy { it.user.fullName })
                    }
                },
                onError = {
                    _state.value = State.Error(it.message)
                }
            )
            .addTo(compositeDisposable)
    }
}