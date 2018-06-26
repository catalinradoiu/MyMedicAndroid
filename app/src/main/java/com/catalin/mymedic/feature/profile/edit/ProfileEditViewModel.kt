package com.catalin.mymedic.feature.profile.edit

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.net.Uri
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.data.User
import com.catalin.mymedic.feature.authentication.AuthenticationValidator
import com.catalin.mymedic.feature.profile.PasswordChangeCallback
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.repository.UsersRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.disposables.CompositeDisposable
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
class ProfileEditViewModel(
    private val usersRepository: UsersRepository,
    val firebaseStorage: FirebaseStorage,
    val authenticationValidator: AuthenticationValidator
) : ViewModel() {

    lateinit var user: User

    val userName = ObservableField<String>("")
    val userEmail = ObservableField<String>("")
    val userBirthDate = ObservableLong(0)
    val userImage = MutableLiveData<String>()
    val userGender = MutableLiveData<Gender>()

    val oldPassword = ObservableField<String>("")
    val newPassword = ObservableField<String>("")
    val newPasswordConfirmation = ObservableField<String>("")

    val profileUpdateResult = SingleLiveEvent<OperationResult>()
    val passwordChangeResult = SingleLiveEvent<OperationResult>()

    val validName = SingleLiveEvent<Boolean>()

    val validOldPassword = SingleLiveEvent<Boolean>()
    val validNewPassword = SingleLiveEvent<Boolean>()
    val passwordsMatch = SingleLiveEvent<Boolean>()

    val state = ObservableField<StateLayout.State>()
    var uploadedImage: Uri? = null

    private val disposables = CompositeDisposable()

    fun initUserProfile(userId: String) {
        state.set(StateLayout.State.LOADING)
        disposables.add(usersRepository.getUserById(userId).mainThreadSubscribe(Consumer {
            user = it
            userName.set(it.displayName)
            userEmail.set(it.email)
            userBirthDate.set(it.birthDate)
            userImage.value = it.imageUrl
            userGender.value = it.gender
            state.set(StateLayout.State.NORMAL)
        }, Consumer {
            state.set(StateLayout.State.ERROR)
        }))
    }

    fun updateProfile(userId: String) {
        validName.value = authenticationValidator.isValidName(userName.get().orEmpty())
        if (validName.value == true) {
            state.set(StateLayout.State.LOADING)
            user.apply {
                displayName = userName.get().orEmpty()
                birthDate = userBirthDate.get()
            }
            disposables.add(usersRepository.updateUser(user).andThen(usersRepository.updateUserImage(userId, uploadedImage)).mainThreadSubscribe(Action {
                state.set(StateLayout.State.NORMAL)
                profileUpdateResult.value = OperationResult.Success()
            }, Consumer {
                state.set(StateLayout.State.NORMAL)
                profileUpdateResult.value = OperationResult.Error(it.localizedMessage.orEmpty())
            }))
        }
    }

    fun updatePassword() {
        val oldPasswordValid = authenticationValidator.isValidPassword(oldPassword.get().orEmpty())
        val newPasswordValid = authenticationValidator.isValidPassword(newPassword.get().orEmpty())

        validNewPassword.value = newPasswordValid
        validOldPassword.value = oldPasswordValid

        if (oldPasswordValid && newPasswordValid) {
            passwordsMatch.value = newPassword.get().orEmpty() == newPasswordConfirmation.get().orEmpty()
            if (passwordsMatch.value == true) {
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(
        private val usersRepository: UsersRepository,
        private val firebaseStorage: FirebaseStorage,
        private val authenticationValidator: AuthenticationValidator
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = ProfileEditViewModel(usersRepository, firebaseStorage, authenticationValidator) as T
    }
}