package com.catalin.mymedic.feature.profile.edit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.net.Uri
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.data.User
import com.catalin.mymedic.feature.profile.PasswordChangeCallback
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.functions.Action
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

    lateinit var user: User

    val userName = ObservableField<String>("")
    val userEmail = ObservableField<String>("")
    val userBirthDate = ObservableLong(0)
    val userImage = MutableLiveData<String>()
    val userGender = MutableLiveData<Gender>()

    val oldPassword = ObservableField<String>("")
    val newPassword = ObservableField<String>("")
    val newPasswordConfirmation = ObservableField<String>("")

    val profileUpdateSuccess = SingleLiveEvent<Boolean>()
    val passwordChangeResult = SingleLiveEvent<OperationResult>()

    val state = ObservableField<StateLayout.State>()
    var uploadedImage: Uri? = null

    fun initUserProfile(userId: String) {
        state.set(StateLayout.State.LOADING)
        usersRepository.getUserById(userId).mainThreadSubscribe(Consumer {
            user = it
            userName.set(it.displayName)
            userEmail.set(it.email)
            userBirthDate.set(it.birthDate)
            userImage.value = it.imageUrl
            userGender.value = it.gender
            state.set(StateLayout.State.NORMAL)
        }, Consumer {
            state.set(StateLayout.State.ERROR)
        })
    }

    fun updateProfile(userId: String) {
        user.apply {
            displayName = userName.get().orEmpty()
            birthDate = userBirthDate.get()
        }
        usersRepository.updateUser(user).andThen(usersRepository.updateUserImage(userId, uploadedImage)).mainThreadSubscribe(Action {
            profileUpdateSuccess.value = true
        }, Consumer {
            profileUpdateSuccess.value = false
        })

    }

    fun updatePassword() {
        usersRepository.reauthenticateUser(user.email, oldPassword.get().orEmpty(), object : PasswordChangeCallback {
            override fun onSuccess() {
                usersRepository.changeUserPassword(newPassword.get().orEmpty(), object : PasswordChangeCallback {
                    override fun onSuccess() {
                        passwordChangeResult.value = OperationResult.Success()
                        newPassword.set("")
                        oldPassword.set("")
                        newPasswordConfirmation.set("")
                    }

                    override fun onError(errorMessage: String) {
                        passwordChangeResult.value = OperationResult.Error(errorMessage)
                    }

                })
            }

            override fun onError(errorMessage: String) {
                passwordChangeResult.value = OperationResult.Error(errorMessage)
            }

        })
    }

    class Factory @Inject constructor(private val usersRepository: UsersRepository, private val firebaseStorage: FirebaseStorage) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileEditViewModel(usersRepository, firebaseStorage) as T
    }
}