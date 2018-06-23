package com.catalin.mymedic.feature.profile.edit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.net.Uri
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/23/2018
 */
/*
    Firebase storage should not be in the view model, but because it is needed in order to load the images, this situation and other similar cases
    are exceptions
*/
class ProfileEditViewModel(private val usersRepository: UsersRepository, val firebaseStorage: FirebaseStorage) : ViewModel() {

    val userName = ObservableField<String>("")
    val userEmail = ObservableField<String>("")
    val userBirthDate = ObservableLong(0)
    val userImage = MutableLiveData<String>()

    val state = ObservableField<StateLayout.State>()
    var uploadedImage: Uri? = null

    fun initUserProfile(userId: String) {
        state.set(StateLayout.State.LOADING)
        usersRepository.getUserById(userId).mainThreadSubscribe(Consumer {
            userName.set(it.displayName)
            userEmail.set(it.email)
            userBirthDate.set(it.birthDate)
            userImage.value = it.imageUrl
            state.set(StateLayout.State.NORMAL)
        }, Consumer {
            state.set(StateLayout.State.ERROR)
        })
    }

    fun updateProfile(userId: String) {
        uploadedImage?.let {
            usersRepository.updateUserImage(userId, it).mainThreadSubscribe(Consumer {

            }, Consumer {

            })
        }
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository, private val firebaseStorage: FirebaseStorage) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileEditViewModel(usersRepository, firebaseStorage) as T

    }
}