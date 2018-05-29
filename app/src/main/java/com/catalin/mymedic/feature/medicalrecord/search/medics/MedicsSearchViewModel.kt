package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.data.User
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsSearchViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    // TODO : Replace the observable field for this users list with live data
    // TODO : Replace the error observable boolean with SingleLiveData -> this applies to multiple places
    val medicsList = ObservableField<List<User>>()
    val state = ObservableField<StateLayout.State>()
    val isFilterInProgress = ObservableBoolean(false)
    val filteringName = ObservableField<String>("")
    val isError = ObservableBoolean(false)

    private val disposables = CompositeDisposable()

    fun getMedicsList(specialtyId: Int) {
        state.set(StateLayout.State.LOADING)
        disposables.add(usersRepository.getMedicsBySpecialty(specialtyId).mainThreadSubscribe(Consumer { result ->
            if (result.isEmpty()) {
                state.set(StateLayout.State.EMPTY)
            } else {
                state.set(StateLayout.State.NORMAL)
                medicsList.set(result)
            }
        }, Consumer { exception ->
            if (exception is NoSuchElementException) {
                // no elements found
                state.set(StateLayout.State.EMPTY)
            } else {
                state.set(StateLayout.State.ERROR)
            }
        }))
    }

    fun getFilteredMedics(specialtyId: Int) {
        disposables.clear()
        isFilterInProgress.set(true)
        Observable.timer(SEARCH_REQUEST_DELAY_TIME, TimeUnit.MILLISECONDS)
            .flatMap({ usersRepository.getFilteredMedicsList(specialtyId, filteringName.get().orEmpty()).toObservable() }).mainThreadSubscribe(
            Consumer
            { result ->
                isError.set(false)
                isFilterInProgress.set(false)
                if (result.isEmpty()) {
                    state.set(StateLayout.State.EMPTY)
                } else {
                    state.set(StateLayout.State.NORMAL)
                    medicsList.set(result)
                }
            },
            Consumer
            {
                isFilterInProgress.set(false)
                if (medicsList.get()?.isEmpty() == true) {
                    isError.set(true)
                } else {
                    state.set(StateLayout.State.ERROR)
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            MedicsSearchViewModel(usersRepository) as T
    }

    companion object {
        private const val SEARCH_REQUEST_DELAY_TIME = 1000L
    }
}