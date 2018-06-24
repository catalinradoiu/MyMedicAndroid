package com.catalin.mymedic.storage.source

import android.net.Uri
import com.catalin.mymedic.data.User
import com.catalin.mymedic.data.toMap
import com.catalin.mymedic.feature.profile.PasswordChangeCallback
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote source for the users based on Firebase
 *
 * @author catalinradoiu
 * @since 2/21/2018
 */
@Singleton
class UsersFirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) {

    /**
     * Get as parameters the user and his password
     * Registers the user then sends the corresponding email
     * @return a Completable with the status of the operation
     */
    fun registerUser(user: User, password: String): Completable =
        RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, user.email, password).flatMapCompletable {
            it.user.sendEmailVerification()
            RxFirebaseDatabase.setValue(
                firebaseDatabase.getReference(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(it.user.uid),
                user.apply {
                    id = it.user.uid
                }
            )
        }

    fun getUserByEmailAndPassword(email: String, password: String): Single<AuthResult> =
        RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password).toSingle()

    fun sendPasswordResetEmail(email: String) = RxFirebaseAuth.sendPasswordResetEmail(firebaseAuth, email)

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun getUserById(userId: String): Single<User> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(userId),
            User::class.java
        ).toSingle()

    fun getMedicsBySpecialty(specialtyId: Int, currentUserId: String): Single<List<User>> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).orderByChild(
                FirebaseDatabaseConfig.USERS_TABLE_SPECIALISATION_ID_COLUMN
            ).equalTo(
                specialtyId.toDouble()
            ), { data ->
                data.children.mapNotNull { value -> value.getValue(User::class.java) }
                    .filter { medic -> medic.specialisationId == specialtyId && medic.id != currentUserId }
            }).toSingle()

    fun updateUserNotificationToken(token: String?, userId: String) {
        firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(userId).child(FirebaseDatabaseConfig.USERS_NOTIFICATION_TOKEN)
            .setValue(token)
    }

    fun updateUser(user: User): Completable =
        RxFirebaseDatabase.updateChildren(firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(user.id), user.toMap())

    fun updateUserImage(userId: String, userImage: Uri): Completable = RxFirebaseStorage.putFile(
        firebaseStorage.reference.child(FirebaseDatabaseConfig.USERS_IMAGES_FOLDER + userId + FirebaseDatabaseConfig.USER_IMAGE_EXTENSTION),
        userImage
    ).toCompletable()

    fun reauthenticateUser(email: String, password: String, callback: PasswordChangeCallback) {
        firebaseAuth.currentUser?.reauthenticate(EmailAuthProvider.getCredential(email, password))?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                callback.onSuccess()
            } else {
                callback.onError(task.exception?.localizedMessage.orEmpty())
            }
        })
    }

    fun changeUserPassword(newPassword: String, callback: PasswordChangeCallback) {
        firebaseAuth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener {
            if (it.isSuccessful) {
                callback.onSuccess()
            } else {
                callback.onError(it.exception?.localizedMessage.orEmpty())
            }
        }
    }

    fun logOutUser(userId: String) {
        firebaseAuth.signOut()
        updateUserNotificationToken("", userId)
    }

}
