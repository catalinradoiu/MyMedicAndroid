package com.catalin.mymedic.feature.profile.edit

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.net.Uri
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/23/2018
 */
class ProfileEditViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    val userName = ObservableField<String>("")
    val userEmail = ObservableField<String>("")
    val userBirthDate = ObservableLong(0)

    var uploadedImage: Uri? = null

    fun updateProfile() {
        uploadedImage?.let {
            usersRepository.updateUserImage(usersRepository.getCurrentUserId(), it).mainThreadSubscribe(Consumer {

            }, Consumer {

            })
        }
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileEditViewModel(usersRepository) as T

    }
}