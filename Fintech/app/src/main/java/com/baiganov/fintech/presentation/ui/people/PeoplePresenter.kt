package com.baiganov.fintech.presentation.ui.people

import com.baiganov.fintech.domain.repository.PeopleRepository
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.model.UserToUserFingerPrintMapper
import com.baiganov.fintech.util.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class PeoplePresenter @Inject constructor(
    private val repository: PeopleRepository,
    private val userToUserFingerPrintMapper: UserToUserFingerPrintMapper,
    private val networkManager: NetworkManager
) : MvpPresenter<PeopleView>() {

    private val compositeDisposable = CompositeDisposable()
    private val searchSubject: PublishSubject<String> = PublishSubject.create()
    private var isLoading = true

    init {
        searchUsers()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUsers()
        searchUsers("")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun searchUsers(searchQuery: String) {
        searchSubject.onNext(searchQuery)
    }

    private fun loadUsers() {
        repository.loadUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewState.render(State.Loading)
            }
            .subscribeBy(
                onComplete = {
                    Functions.EMPTY_ACTION
                    isLoading = false
                },
                onError = { exception ->
                    viewState.render(State.Error(exception.message))
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
                        viewState.render(State.Loading)
                    } else {
                        viewState.render(State.Result(users.sortedBy { it.user.fullName }))
                    }
                },
                onError = {
                    viewState.render(State.Error(it.message))
                }
            )
            .addTo(compositeDisposable)
    }
}