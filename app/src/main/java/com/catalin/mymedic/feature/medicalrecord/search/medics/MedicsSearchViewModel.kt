package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.util.Log
import com.catalin.mymedic.data.User
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

    private val disposables = CompositeDisposable()

    fun initMedicsList(specialtyId: Int) {
        usersRepository.getMedicsBySpecialty(specialtyId).mainThreadSubscribe(Consumer { result ->
            medicsList.set(result)
        }, Consumer { exception ->
            if (exception is NoSuchElementException) {
                Log.d("firebase", "no data found")
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MedicsSearchViewModel(usersRepository) as T
    }
}