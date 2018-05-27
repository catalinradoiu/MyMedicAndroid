package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.User
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsSearchViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    val medicsList = ObservableField<List<User>>()
    val state = ObservableField<StateLayout.State>()

    private val disposables = CompositeDisposable()

    fun initMedicsList(specialtyId: Int) {
        state.set(StateLayout.State.LOADING)
        usersRepository.getMedicsBySpecialty(specialtyId).mainThreadSubscribe(Consumer { result ->
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
}