package com.catalin.mymedic.feature.profile

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/22/2018
 */
class ProfileViewModel(private val usersRepository: UsersRepository, val firebaseStorage: FirebaseStorage) : ViewModel() {

    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)
    val userName = ObservableField<String>("")
    val userEmail = ObservableField<String>("")
    val userBirthDate = ObservableLong()
    val userGender = ObservableField<Gender>()

    val profileImage = MutableLiveData<String>()

    private val disposables = CompositeDisposable()

    fun getCurrentUserDetails() {
        state.set(StateLayout.State.LOADING)
        disposables.add(usersRepository.getUserById(usersRepository.getCurrentUserId()).mainThreadSubscribe(Consumer {
            state.set(StateLayout.State.NORMAL)
            userName.set(it.displayName)
            userEmail.set(it.email)
            userBirthDate.set(it.birthDate)
            userGender.set(it.gender)
            profileImage.value = it.imageUrl
        }, Consumer {
            state.set(StateLayout.State.ERROR)
        }))
    }

    fun getCurrentUserId() = usersRepository.getCurrentUserId()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository, private val firebaseStorage: FirebaseStorage) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileViewModel(usersRepository, firebaseStorage) as T

    }
}