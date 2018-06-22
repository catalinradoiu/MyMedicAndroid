package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.User
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.source.UsersFirebaseSource
import com.google.firebase.auth.AuthResult
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing the users
 *
 * @author catalinradoiu
 * @since 2/19/2018
 */
@Singleton
class UsersRepository @Inject constructor(private val usersFirebaseSource: UsersFirebaseSource, private val preferencesManager: SharedPreferencesManager) {

    private var usersList = ArrayList<User>()

    /**
     * Calls the remote source to register the user
     * @return a Completable with the status of the operation
     */
    fun registerUser(user: User, password: String): Completable = usersFirebaseSource.registerUser(user, password)

    fun getUserByEmailAndPassword(email: String, password: String): Single<AuthResult> = usersFirebaseSource.getUserByEmailAndPassword(email, password)

    fun getUserById(userId: String): Single<User> = usersFirebaseSource.getUserById(userId)

    fun sendPasswordResetEmail(email: String) = usersFirebaseSource.sendPasswordResetEmail(email)

    fun getCurrentUser() = usersFirebaseSource.getCurrentUser()

    fun getMedicsBySpecialty(specialtyId: Int): Single<List<User>> {
        val cachedMedics = usersList.filter { it.specialisationId == specialtyId }
        return if (cachedMedics.isEmpty()) usersFirebaseSource.getMedicsBySpecialty(
            specialtyId,
            preferencesManager.currentUserId
        ).doOnSuccess { medics -> usersList.addAll(medics) } else Single.just(
            cachedMedics
        )
    }

    fun getFilteredMedicsList(specialtyId: Int, name: String): Single<List<User>> =
        Single.just(usersList.filter { it.specialisationId == specialtyId && it.displayName.contains(name) })

    fun updateUserNotificationToken(userToken: String) {
        usersFirebaseSource.updateUserNotificationToken(userToken, preferencesManager.currentUserId)
    }

    fun updateUserLocalData(user: User, userId: String) {
        preferencesManager.apply {
            currentUserAvatar = user.imageUrl
            currentUserSpecialty = user.specialisationId
            currentUserName = user.displayName
            currentUserId = userId
        }
    }
}
